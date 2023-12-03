package com.greenjon902.hisdoc;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextType;
import com.greenjon902.hisdoc.pages.*;
import com.greenjon902.hisdoc.sessionHandler.impl.testSessionHandlerImpl.TestSessionHandlerImpl;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.greenjon902.hisdoc.sessionHandler.SessionHandler.VerifyResult.*;

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
		
		return Map.ofEntries(Map.entry("/" + pageNamePrefix, new HomePageRenderer()),
				Map.entry("/" + pageNamePrefix + "event", new EventPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tag", new TagPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tags", new TagsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "person", new PersonPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "persons", new PersonsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "timeline", new TimelinePageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "addS", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(NO_SESSION))),
				Map.entry("/" + pageNamePrefix + "addI", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(INVALID_IP))),
				Map.entry("/" + pageNamePrefix + "addV", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID))),
				Map.entry("/" + pageNamePrefix + "addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID))),
				Map.entry("/" + pageNamePrefix + "add", new PageRenderer() {  // A helper page for choosing what to happen on adding
					@Override
					public String render(Map<String, String> query, String fragment, User user) {
						PageBuilder pageBuilder = new PageBuilder();
						pageBuilder.add(new NavBarBuilder(pageBuilder));
						pageBuilder.add(new TextBuilder(TextType.NORMAL, "\n") {{
							add("This is a testing page, please contact jon if your seeing this (given your not a developer)!\n");
							add("NO_SESSION", "addS");
							add("INVALID_IP", "addI");
							add("VALID", "addV");
						}});

						return pageBuilder.render(user);
					}
				}),
				Map.entry("/" + pageNamePrefix + "themes", new CssPageRenderer()));
	}
}
