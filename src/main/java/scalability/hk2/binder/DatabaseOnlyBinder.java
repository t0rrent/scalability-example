package scalability.hk2.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import jakarta.inject.Singleton;
import scalability.hk2.factory.DataSourceConfigFactory;
import scalability.model.DataSourceConfig;
import scalability.service.DatabaseInitializerLifeCycle;
import scalability.service.JDBCService;
import scalability.service.LifeCycle;
import scalability.service.PostgresJDBCService;

public class DatabaseOnlyBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bindFactory(DataSourceConfigFactory.class).to(DataSourceConfig.class).in(Singleton.class);
		bind(DatabaseInitializerLifeCycle.class).to(LifeCycle.class).in(Singleton.class).ranked(100);
		bind(PostgresJDBCService.class).to(JDBCService.class).in(Singleton.class);
	}

}
