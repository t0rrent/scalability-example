package scalability.model;

public class DataSourceConfig {
	
	private String hostName;
	
	private String database;
	
	private String user;
	
	private String password;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(final String hostName) {
		this.hostName = hostName;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(final String database) {
		this.database = database;
	}

	public String getUser() {
		return user;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

}
