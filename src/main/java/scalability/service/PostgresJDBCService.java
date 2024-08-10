package scalability.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import scalability.model.DataSourceConfig;
import scalability.type.ThrowingConsumer;
import scalability.util.IOUtils;

public class PostgresJDBCService implements JDBCService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostgresJDBCService.class);
	
	private final DataSourceConfig dataSourceConfig;
	
	private final ThreadLocal<Connection> connection;
	
	@Inject
	public PostgresJDBCService(final DataSourceConfig dataSourceConfig) {
		this.dataSourceConfig = dataSourceConfig;
		this.connection = new ThreadLocal<>();
	}

	@Override
	public Timestamp selectTimestamp(final String scriptName) throws FileNotFoundException, IOException, SQLException {
		final AtomicReference<Timestamp> returnValue = new AtomicReference<>();
		getConnectionAndExecute((connection) -> {
			try (Statement statement = connection.createStatement()) {
				final String sql = getSql(scriptName);
				final boolean hasResults = statement.execute(sql);
				if (hasResults) {
					try (final ResultSet resultSet = statement.getResultSet()) {
						if (resultSet.next()) {
							returnValue.set((Timestamp) resultSet.getTimestamp(1));
						}
					}
				}
			} catch (final IOException exception) {
				LOGGER.error("Script error", exception);
			}
		});
		return returnValue.get();
	}

	@Override
	public void executeScript(final String scriptName) throws FileNotFoundException, IOException, SQLException {
		final String sql = getSql(scriptName);
		executeSql(sql);
	}

	@Override
	public void executeSql(final String sql) throws FileNotFoundException, IOException, SQLException {
		getConnectionAndExecute((connection) -> {
			try (Statement statement = connection.createStatement()) {
				LOGGER.debug("executing:\n" + sql);
				final boolean hasResults = statement.execute(sql);
				if (hasResults) {
					throw new IllegalStateException("Results are not expected. Script: " + sql);
				}
			}
		});
	}

	private String getSql(final String scriptName) throws IOException {
		return IOUtils.readResourceAsString(scriptName);
	}

	private void getConnectionAndExecute(final ThrowingConsumer<Connection, SQLException> action) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			action.accept(connection);
			connection.commit();
		} catch (final SQLException exception) {
			if (connection != null && !connection.isClosed()) {
				connection.rollback();
			}
			throw exception;
		} finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			this.connection.remove();
		}
	}

	private Connection getConnection() throws SQLException {
		Connection localConnection = this.connection.get();
		if (localConnection == null) {
			localConnection = createNewConnection();
			this.connection.set(localConnection);
		}
		return localConnection;
	}

	private Connection createNewConnection() throws SQLException {
		final String url = "jdbc:postgresql://" + dataSourceConfig.getHostName() + "/" + dataSourceConfig.getDatabase();
		final Connection connection = DriverManager.getConnection(url, dataSourceConfig.getUser(), dataSourceConfig.getPassword());
		connection.setAutoCommit(false);
		return connection;
	}

}
