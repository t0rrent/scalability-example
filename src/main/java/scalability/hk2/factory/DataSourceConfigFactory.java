package scalability.hk2.factory;

import org.glassfish.hk2.api.Factory;

import scalability.model.DataSourceConfig;

public class DataSourceConfigFactory implements Factory<DataSourceConfig> {

	@Override
	public DataSourceConfig provide() {
		final DataSourceConfig dataSourceConfig = new DataSourceConfig();
		dataSourceConfig.setHostName("localhost");
		dataSourceConfig.setDatabase("scalability_example");
		dataSourceConfig.setUser("postgres");
		dataSourceConfig.setPassword("postgres");
		return dataSourceConfig;
	}

	@Override
	public void dispose(final DataSourceConfig instance) {
	}

}
