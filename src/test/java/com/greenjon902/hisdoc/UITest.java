package com.greenjon902.hisdoc;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextType;
import com.greenjon902.hisdoc.pages.*;
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
import java.util.logging.Logger;

import static com.greenjon902.hisdoc.SessionHandler.VerifyResult.*;
import static com.greenjon902.hisdoc.Utils.getTestLogger;

public class UITest {

	public static void main(String[] args) throws Exception {
		Logger logger = getTestLogger();

		DB database = DB.newEmbeddedDB(3306);
		database.start();

		HashMap<String, PageRenderer> map = new HashMap<>();
		map.putAll(createTheThing(database, "HisDocUITest_Refined", "UITestSetup_Refined", "", logger));
		map.putAll(createTheThing(database, "HisDocUITest_Large", "UITestSetup_Large", "l/", logger));

		WebDriver webDriver = new WebDriver(new WebDriverConfig(
				map,
				8080, 0, 0, "com/greenjon902/hisdoc/logo.ico"
		), logger);
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
	private static Map<String, PageRenderer> createTheThing(DB database, String dbName, String sqlScriptName, String pageNamePrefix, Logger logger) throws ManagedProcessException, SQLException {
		database.createDB(dbName);
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName + "?allowMultiQueries=true");

		Dispatcher dispatcher = new Dispatcher(connection, logger);
		dispatcher.createTables();
		dispatcher.prepare(sqlScriptName).execute();  // Fill with test data
		
		return Map.ofEntries(Map.entry("/" + pageNamePrefix, new HomePageRenderer()),
				Map.entry("/" + pageNamePrefix + "event", new EventPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tag", new TagPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tags", new TagsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "person", new PersonPageRenderer(dispatcher, new TestMcPlaytimeSupplierImpl())),
				Map.entry("/" + pageNamePrefix + "persons", new PersonsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "timeline", new TimelinePageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "addS", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(NO_SESSION, false), false)),
				Map.entry("/" + pageNamePrefix + "addI", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(INVALID_IP, false), false)),
				Map.entry("/" + pageNamePrefix + "addV", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID, false), false)),
				Map.entry("/c/" + pageNamePrefix + "addV", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID, true), false)),
				Map.entry("/" + pageNamePrefix + "addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID, false))),
				Map.entry("/c/" + pageNamePrefix + "addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID, true))),
				Map.entry("/" + pageNamePrefix + "add", new HtmlPageRenderer() {  // A helper page for choosing what to happen on adding
					@Override
					public String render(Map<String, String> query, String fragment, User user) {
						PageBuilder pageBuilder = new PageBuilder();
						pageBuilder.add(new NavBarBuilder(pageBuilder));
						pageBuilder.add(new TextBuilder(TextType.NORMAL, "\n", null) {{
							add("This is a testing page, please contact jon if your seeing this (given your not a developer)!\n");
							add("NO_SESSION", "addS", false);
							add("INVALID_IP", "addI", false);
							add("VALID", "addV", false);
							add("VALID & CONSUME", "c/addV", false);
						}});

						return pageBuilder.render(user);
					}
				}),
				Map.entry("/" + pageNamePrefix + "themes", new CssPageRenderer()));
	}
}


class TestSessionHandlerImpl implements SessionHandler {
	private final VerifyResult verifyResult;
	private final boolean consumeVerifications;  // It won't actually consume them, but it will say whether it did or not

	public TestSessionHandlerImpl(VerifyResult verifyResult, boolean consumeVerifications) {
		this.verifyResult = verifyResult;
		this.consumeVerifications = consumeVerifications;
	}

	@Override
	public VerifyResult verify(User user, Map<String, String> query) {
		return verifyResult;
	}

	@Override
	public String getNameOf(User user, Map<String, String> query) {
		return "TestUserName";
	}

	@Override
	public boolean suggestConsumeVerification(User user, Map<String, String> query) {
		if (verifyResult != VerifyResult.VALID) {
			throw new RuntimeException("Cannot unverify user " + user + " as is not verified in the first place");
		}
		return consumeVerifications;
	}

	@Override
	public int getPersonId(User user, Map<String, String> query) {
		if (verifyResult != VerifyResult.VALID) {
			throw new RuntimeException("Cannot get user " + user + " as is not verified");
		}
		return 1;
	}

	@Override
	public String getPostUrl(int personId) {
		if (verifyResult != VerifyResult.VALID) {
			throw new IllegalStateException("Person with " + personId + " cannot add another event");
		}
		return "http://127.0.0.1:8080/addv";
	}
}

class TestMcPlaytimeSupplierImpl implements McPlaytimeSupplier {
	@Override
	public int getTicks(String uuid) {
		return 0;
	}
}