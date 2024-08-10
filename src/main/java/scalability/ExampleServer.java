package scalability;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.UriBuilder;
import scalability.filter.CorsFilter;
import scalability.hk2.binder.MainBinder;
import scalability.provider.ServiceLocatorProvider;
import scalability.service.LifeCycle;
import scalability.servlet.SimpleHttpServlet;
import scalability.util.CollectionUtils;
import scalability.util.SimpleConfigUtil;

public class ExampleServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleServer.class);
	
	private static final String ALL_IN_ONE_LAYER = "allInOne";

	private static final String PRESENTATION_LAYER = "presentation";

	private static final String APPLICATION_LAYER = "application";
	
	private static final String DATABASE_LAYER = "database";

	private final Collection<Supplier<Binder>> binders;

	private final Collection<Supplier<Binder>> databaseOnlyBinders;

	private final Collection<Supplier<Application>> applications;

	private final String layerConfig;
	
	private Server server;

	private ServiceLocator serviceLocator;

	private ServiceLocatorProvider serviceLocatorProvider;

	public ExampleServer(
			final Collection<Supplier<Binder>> binders,
			final Collection<Supplier<Binder>> databaseOnlyBinders,
			final Collection<Supplier<Application>> applications
	) {
		this.binders = binders;
		this.databaseOnlyBinders = databaseOnlyBinders;
		this.applications = applications;
		this.layerConfig = SimpleConfigUtil.getConfig("layer", ALL_IN_ONE_LAYER);

		if (layerConfig.equals(DATABASE_LAYER)) {
			initServiceLocator();
		} else {
			initServer();
		}
	}
	
	private void initServiceLocator() {
		this.serviceLocatorProvider = new ServiceLocatorProvider() {
			@Override
			public ServiceLocator getServiceLocator() {
				return ServiceLocatorUtilities.bind();
			}
		};
	}

	private void initServer() {
		final String uri = SimpleConfigUtil.getConfig("serverUrl", "http://localhost");
		final int port = SimpleConfigUtil.getConfig("serverPort", 80);
		
		final URI baseUri = UriBuilder.fromUri(uri)
				.port(port)
				.build();
		final String corsFilter = uri;
		CorsFilter.WEB_CLIENT_HOST = corsFilter;
		
		final List<Handler> handlers = new ArrayList<>();
		if (layerConfig.equals(ALL_IN_ONE_LAYER) || layerConfig.equals(APPLICATION_LAYER)) {
			handlers.add(createAPIServletContext());
		}
		if (layerConfig.equals(ALL_IN_ONE_LAYER) || layerConfig.equals(PRESENTATION_LAYER)) {
	        handlers.add(createWebServletContext());
		}
        
        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(handlers.toArray(new Handler[handlers.size()]));
        
        server = JettyHttpContainerFactory.createServer(baseUri, false);
        server.setHandler(contexts);
	}

	private Handler createWebServletContext() {
		final ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(SimpleHttpServlet.class, "/*");
		return servletContextHandler;
	}

	private Handler createAPIServletContext() {
		final ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/api");
        
        final Set<Class<?>> apiClasses = applications.stream()
        		.map(Supplier::get)
        		.map(Application::getClasses)
        		.flatMap(Collection::stream)
        		.collect(Collectors.toSet());
        
    	final ResourceConfig resourceConfig = new ResourceConfig();
    	resourceConfig.register(new CorsFilter());
        resourceConfig.registerClasses(apiClasses);
        
		final ServletContainer servletContainer = new ServletContainer(resourceConfig);
		final ServletHolder servletHolder = new ServletHolder(servletContainer);
        servletContextHandler.addServlet(servletHolder, "/*");

        this.serviceLocatorProvider = new ServiceLocatorProvider();
        resourceConfig.register(serviceLocatorProvider);
        
        return servletContextHandler;
	}

	private void startServer() {
        try {
			server.start();
        } catch (final Exception exception) {
        	LOGGER.error("problem starting server", exception);
        }
	}
	
	private void startServices() {
		try {
			if (!layerConfig.equals(PRESENTATION_LAYER)) {
				this.serviceLocator = this.serviceLocatorProvider.getServiceLocator();
				ServiceLocatorUtilities.enableImmediateScopeSuspended(serviceLocator);
				ServiceLocatorUtilities.bind(this.serviceLocator, new AbstractBinder() {
					@Override
					protected void configure() {
						bind(serviceLocator).to(ServiceLocator.class);
					}
				});
				final Collection<Supplier<Binder>> binders = layerConfig.equals(APPLICATION_LAYER) ? this.binders : (
						layerConfig.equals(ALL_IN_ONE_LAYER) ? Stream.concat(this.binders.stream(), this.databaseOnlyBinders.stream()).toList() : databaseOnlyBinders
				);
				ServiceLocatorUtilities.bind(this.serviceLocator, new MainBinder(binders));
			}
	    } catch (final Exception exception) {
	    	LOGGER.error("problem starting server", exception);
	    }
	}

	private void stopServer() {
		try {
			server.stop();
		} catch (final Exception exception) {
        	LOGGER.error("problem stopping server", exception);
		}
	}
	
	public synchronized void start() {
		if (!layerConfig.equals(DATABASE_LAYER)) {
			startServer();
		}
		startServices();
		if (!layerConfig.equals(PRESENTATION_LAYER)) {
			final List<ServiceHandle<LifeCycle>> serviceHandles = serviceLocator.getAllServiceHandles(LifeCycle.class);
			Collections.sort(serviceHandles, CollectionUtils.reverseComparator(
					Comparator.comparingInt((serviceHandle) -> serviceHandle.getActiveDescriptor().getRanking())
			));
			serviceHandles.stream()
					.map(ServiceHandle::getService)
					.forEach(LifeCycle::start);
		}
		Runtime.getRuntime()
				.addShutdownHook(new Thread(this::stop));
	}

	public synchronized void stop() {
		if (!layerConfig.equals(PRESENTATION_LAYER)) {
			serviceLocator.getAllServices(LifeCycle.class).forEach(LifeCycle::stop);
		}
		if (!layerConfig.equals(DATABASE_LAYER)) {
			stopServer();
		}
	}
	
}
