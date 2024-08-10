package scalability.provider;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

public class ServiceLocatorProvider implements ContainerLifecycleListener {

	private ServiceLocator serviceLocator;

	@Override
	public void onStartup(final Container container) {
		this.serviceLocator = container.getApplicationHandler().getInjectionManager().getInstance(ServiceLocator.class);
	}

	@Override
	public void onReload(final Container container) {
	}

	@Override
	public void onShutdown(final Container container) {
	}

	public ServiceLocator getServiceLocator() {
		return serviceLocator;
	}

}
