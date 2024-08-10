package scalability;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.glassfish.hk2.utilities.Binder;

import jakarta.ws.rs.core.Application;
import scalability.api.ExampleAPI;
import scalability.api.SimpleCrudAPI;
import scalability.hk2.binder.DatabaseOnlyBinder;
import scalability.hk2.binder.ScalabilityExampleCoreBinder;
import scalability.hk2.binder.ScalabilityExampleModuleBinder;
import scalability.hk2.binder.ScalabilityExampleMyBatisBinder;

public class ScalabilityExampleApplication extends Application {

	private static final Collection<Supplier<Binder>> APPLICATION_BINDERS = Arrays.asList(
			ScalabilityExampleModuleBinder::new,
			ScalabilityExampleMyBatisBinder::new,
			ScalabilityExampleCoreBinder::new
	);

	private static final Collection<Supplier<Binder>> DATABASE_ONLY_BINDERS = Arrays.asList(
			DatabaseOnlyBinder::new
	);

	private static final Collection<Supplier<Application>> APPLICATION_CLASSES = Arrays.asList(
			ScalabilityExampleApplication::new
	);
	
	public static void main(final String[] args) {
		final ExampleServer server = new ExampleServer(APPLICATION_BINDERS, DATABASE_ONLY_BINDERS, APPLICATION_CLASSES);
		server.start();
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> apis = new HashSet<>();
		apis.add(ExampleAPI.class);
		apis.add(SimpleCrudAPI.class);
		return apis;
	}
	
}
