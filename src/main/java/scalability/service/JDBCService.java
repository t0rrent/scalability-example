package scalability.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface JDBCService {

	Timestamp selectTimestamp(final String scriptName) throws FileNotFoundException, IOException, SQLException;

	void executeScript(String scriptName) throws FileNotFoundException, IOException, SQLException;

	void executeSql(String sql) throws FileNotFoundException, IOException, SQLException;

}
