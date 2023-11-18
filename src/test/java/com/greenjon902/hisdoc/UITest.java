package com.greenjon902.hisdoc;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.pages.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UITest {
	public static void main(String[] args) throws Exception {
		DB database = DB.newEmbeddedDB(3306);
		database.start();

		HashMap<String, PageRenderer> map = new HashMap<>();
		map.putAll(createTheThing(database, "HisDocUITest_Refined", "UITestSetup_Refined", ""));
		map.putAll(createTheThing(database, "HisDocUITest_Large", "UITestSetup_Large", "l/"));

		WebDriver webDriver = new WebDriver(new WebDriverConfig(
				map,
				8080, 0, 0, "com/greenjon902/hisdoc/logo.ico"
		));
		webDriver.start();
	}

	/**
	 * Creates a new database with the given name, and fills it with information from the given sql file.
	 * Then uses this to create a map from paths to page renderers.
	 *
	 * @param dbName The name of the database to create
	 * @param sqlScriptName The name of the sql script that fills the database with test data
	 * @param pageNamePrefix The text to prefix page names with, can be "", or could be "test/"
	 */
	private static Map<String, PageRenderer> createTheThing(DB database, String dbName, String sqlScriptName, String pageNamePrefix) throws ManagedProcessException, SQLException {
		database.createDB(dbName);
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName + "?allowMultiQueries=true");

		Dispatcher dispatcher = new Dispatcher(connection);
		dispatcher.createTables();
		dispatcher.prepare(sqlScriptName).execute();  // Fill with test data
		
		return Map.of("/" + pageNamePrefix + "event", new EventPageRenderer(dispatcher),
				"/" + pageNamePrefix + "tag", new TagPageRenderer(dispatcher),
				"/" + pageNamePrefix + "tags", new TagsPageRenderer(dispatcher),
				"/" + pageNamePrefix + "person", new PersonPageRenderer(dispatcher),
				"/" + pageNamePrefix + "persons", new PersonsPageRenderer(dispatcher),
				"/" + pageNamePrefix + "timeline", new TimelinePageRenderer(dispatcher),
				"/" + pageNamePrefix + "themes", new PageRenderer() {
					@Override
					public String render(Map<String, String> query, String fragment, User user) throws SQLException {
						try {
							InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/pageBuilder/themes/" + query.get("name") + ".css");
							return new String(fileInputStream.readAllBytes());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});
	}
}
