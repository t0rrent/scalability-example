package scalability.model;

import java.sql.Timestamp;

public class MigrationScript {
	
	private String changeSql;
	
	private String undoSql;
	
	private Timestamp timestamp;
	
	private String description;
	
	private String fileName;

	public String getChangeSql() {
		return changeSql;
	}

	public void setChangeSql(final String changeSql) {
		this.changeSql = changeSql;
	}

	public String getUndoSql() {
		return undoSql;
	}

	public void setUndoSql(final String undoSql) {
		this.undoSql = undoSql;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

}
