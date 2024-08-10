package scalability.service;

import java.util.Collection;

import org.apache.ibatis.session.Configuration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import jakarta.inject.Inject;
import scalability.hk2.binder.SimpleFactoryBinder;
import scalability.hk2.factory.MyBatisMapperFactory;
import scalability.model.MapperClass;
import scalability.model.SimpleAliasClass;

public class MyBatisInitializerLifeCycle implements LifeCycle {
	
	private final ServiceLocator serviceLocator;
	
	private final SessionManagerService sessionManagerService;

	private final Configuration config;
	
	@Inject
	public MyBatisInitializerLifeCycle(
			final ServiceLocator serviceLocator,
			final SessionManagerService sessionManagerService,
			final Configuration config
	) {
		this.serviceLocator = serviceLocator;
		this.sessionManagerService = sessionManagerService;
		this.config = config;
	}

	@Override
	public void start() {
		bindAliases();
		bindMappers();
	}

	@Override
	public void stop() {
	}

	private void bindMappers() {
		@SuppressWarnings("rawtypes")
		final Collection<Class> mapperClasses = serviceLocator.getAllServices(MapperClass.class)
				.stream()
				.map(MapperClass::getType)
				.map((mapperClass) -> (Class) mapperClass)
				.toList();
		mapperClasses.forEach(config::addMapper);
		mapperClasses.forEach(this::bindMapper);
	}

	private <T> void bindMapper(final Class<T> mapperType) {
		ServiceLocatorUtilities.bind(
				serviceLocator,
				new SimpleFactoryBinder<T, T>(new MyBatisMapperFactory<>(mapperType, sessionManagerService), mapperType)
		);
 	}
	
	private void bindAliases() {
		serviceLocator.getAllServices(SimpleAliasClass.class)
				.stream()
				.map(SimpleAliasClass::getType)
				.forEach(config.getTypeAliasRegistry()::registerAlias);
	}
	
}
