package scalability.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import scalability.model.MigrationScript;
import scalability.util.IOUtils;

public class DatabaseInitializerLifeCycle implements LifeCycle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializerLifeCycle.class);
	
	private static final String CREATE_CHANGELOG_IDEMPOTENT_SCRIPT = "scripts/create_changelog_idempotent.sql";
	
	private static final String SELECT_CHANGELOG_LATEST_SCRIPT = "scripts/get_latest_changelog_timestamp.sql";

	private static final String MIGRATION_SCRIPTS_RESOURCE_FOLDER = "scripts/migrations";
	
	private final JDBCService jdbcService;
	
	@Inject
	public DatabaseInitializerLifeCycle(final JDBCService jdbcService) {
		this.jdbcService = jdbcService;
	}

	@Override
	public void start() {
		migrate();
	}

	@Override
	public void stop() {
	}

	private void migrate() {
		try {
			LOGGER.info("Migrating database...");
			createChangelogIdempotent();
			final List<MigrationScript> outstandingMigrations = getOutstandingMigrations();
			for (int i = 0; i < outstandingMigrations.size(); i++) {
				migrateUp(outstandingMigrations.get(i));
			}
			LOGGER.info("Database migrating complete");
		} catch (final IOException | SQLException exception) {
			throw new IllegalStateException("Failed to migrate database", exception);
		}
	}

	private void migrateUp(final MigrationScript migrationScript) throws FileNotFoundException, IOException, SQLException {
		LOGGER.debug("MIGRATING " + migrationScript.getFileName());
		jdbcService.executeSql(migrationScript.getChangeSql());
		updateChangelog(migrationScript.getTimestamp(), migrationScript.getDescription());
		LOGGER.debug("MIGRATION " + migrationScript.getFileName() + " COMPLETE");
	}
	
	private void updateChangelog(final Timestamp timestamp, final String description) throws SQLException, FileNotFoundException, IOException {
		jdbcService.executeSql("INSERT INTO changelog VALUES ('" + timestamp + "'::timestamp, '" + description + "')");
	}

	private void createChangelogIdempotent() throws FileNotFoundException, IOException, SQLException {
		jdbcService.executeScript(CREATE_CHANGELOG_IDEMPOTENT_SCRIPT);
	}
	

	private List<MigrationScript> getOutstandingMigrations() throws FileNotFoundException, IOException, SQLException {
		final Timestamp latestChangelog = getLatestChangelog();
		final List<MigrationScript> migrationScripts = new ArrayList<>(getMigrationScripts());
		Collections.sort(migrationScripts, (migration1, migration2) -> migration1.getTimestamp().compareTo(migration2.getTimestamp()));
		return migrationScripts.stream()
				.filter((migration) -> migration.getTimestamp().compareTo(latestChangelog) > 0)
				.toList();
	}

	private List<MigrationScript> getMigrationScripts() throws IOException {
		return IOUtils.getFilesInResourceFolder(MIGRATION_SCRIPTS_RESOURCE_FOLDER).stream()
				.map(this::getMigrationScript)
				.toList();
	}

	private Timestamp getLatestChangelog() throws FileNotFoundException, IOException, SQLException {
		final Timestamp latest = jdbcService.selectTimestamp(SELECT_CHANGELOG_LATEST_SCRIPT);
		if (latest == null) {
			return new Timestamp(0);
		} else {
			return latest;
		}
	}
	
	private MigrationScript getMigrationScript(final String resourceFile) {
		final String[] resourceFileParts = resourceFile.replace("\\", "/").split("/");
		final String fileName = resourceFileParts[resourceFileParts.length - 1];
		final String timestampString = fileName.substring(0, 14);
		
		String changeSql = "";
		String undoSql = "";
		String description = "";
		
		boolean undo = false;
		try {
			for (final String line : IOUtils.readResourceAsList(resourceFile)) {
				if (line.replaceAll("\\s+", "").startsWith("--//@undo")) {
					undo = true;
				} else if (line.replaceAll("\\s+", "").startsWith("--//") && !undo) {
					description += line.split("//")[1].trim();
				} else if (undo) {
					undoSql += line + "\n";
				} else {
					changeSql += line + "\n";
				}
			}
			final MigrationScript migrationScript = new MigrationScript();
			migrationScript.setChangeSql(changeSql);
			migrationScript.setDescription(description);
			migrationScript.setUndoSql(undoSql);
			migrationScript.setFileName(fileName);
			migrationScript.setTimestamp(getTimestampFromString(timestampString));
			return migrationScript;
		} catch (final IOException e) {
			throw new IllegalStateException("error migrating " + resourceFile, e);
		}
	}

	@SuppressWarnings("deprecation")
	private Timestamp getTimestampFromString(final String timestampString) {
		return new Timestamp(
				Integer.parseInt(timestampString.substring(0, 4)) - 1900,
				Integer.parseInt(timestampString.substring(4, 6)) - 1,
				Integer.parseInt(timestampString.substring(6, 8)),
				Integer.parseInt(timestampString.substring(8, 10)),
				Integer.parseInt(timestampString.substring(10, 12)),
				Integer.parseInt(timestampString.substring(12, 14)),
				0
		);
	}


}
