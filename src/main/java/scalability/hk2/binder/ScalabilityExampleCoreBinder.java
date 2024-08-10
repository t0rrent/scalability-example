package scalability.hk2.binder;

import org.apache.ibatis.session.Configuration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import jakarta.inject.Singleton;
import scalability.hk2.factory.DataSourceConfigFactory;
import scalability.hk2.factory.MyBatisConfigurationFactory;
import scalability.model.DataSourceConfig;
import scalability.service.JDBCService;
import scalability.service.LifeCycle;
import scalability.service.MyBatisInitializerLifeCycle;
import scalability.service.MyBatisSessionManagerService;
import scalability.service.PostgresJDBCService;
import scalability.service.SessionManagerService;

public class ScalabilityExampleCoreBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bindFactory(DataSourceConfigFactory.class).to(DataSourceConfig.class).in(Singleton.class);
		bind(PostgresJDBCService.class).to(JDBCService.class).in(Singleton.class);
		bindFactory(MyBatisConfigurationFactory.class).to(Configuration.class).in(Singleton.class);
		bind(MyBatisInitializerLifeCycle.class).to(LifeCycle.class).in(Singleton.class).ranked(99);
		bind(MyBatisSessionManagerService.class).to(SessionManagerService.class).in(Singleton.class);
	}

}
