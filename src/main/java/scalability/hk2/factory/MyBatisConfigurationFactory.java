package scalability.hk2.factory;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.glassfish.hk2.api.Factory;
import org.postgresql.ds.PGSimpleDataSource;

import jakarta.inject.Inject;
import scalability.model.DataSourceConfig;

public class MyBatisConfigurationFactory implements Factory<Configuration> {
	
	private static final String ENVIRONMENT_ID = "main";
	
	private final DataSourceConfig dataSourceConfig;
	
	@Inject
	public MyBatisConfigurationFactory(final DataSourceConfig dataSourceConfig) {
		this.dataSourceConfig = dataSourceConfig;
	}

	@Override
	public Configuration provide() {
		final TransactionFactory transactionFactory = new JdbcTransactionFactory();
		final Environment environment = new Environment(ENVIRONMENT_ID, transactionFactory, createDataSource());
		final Configuration config = new Configuration();
		config.setMapUnderscoreToCamelCase(true);
		config.setEnvironment(environment);
		return config;
	}

	private DataSource createDataSource() {
		final PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setServerNames(new String[] {dataSourceConfig.getHostName()});  
		dataSource.setDatabaseName(dataSourceConfig.getDatabase());   
		dataSource.setUser(dataSourceConfig.getUser());       
		dataSource.setPassword(dataSourceConfig.getPassword());
		return dataSource;
	}

	@Override
	public void dispose(final Configuration instance) {
	}

}
