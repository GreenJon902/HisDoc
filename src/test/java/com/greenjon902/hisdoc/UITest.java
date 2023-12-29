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
import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static com.greenjon902.hisdoc.SessionHandler.VerifyResult.*;
import static com.greenjon902.hisdoc.Utils.getTestLogger;

public class UITest {

	public static void main(String[] args) throws Exception {
		Logger logger = getTestLogger();

		DB database = DB.newEmbeddedDB(3306);
		database.start();

		SessionHandler sessionHandler = new TestSessionHandlerImpl(Map.of(
				UUID.fromString("0000000-0000-0000-0000-000000000001"), 1
		));
		PermissionHandler permissionHandler = new TestPermissionHandlerImpl(Map.of());

		HashMap<String, PageRenderer> map = new HashMap<>();
		map.putAll(createTheThing(database, "HisDocUITest_Refined", "UITestSetup_Refined", "", logger, permissionHandler));
		map.putAll(createTheThing(database, "HisDocUITest_Large", "UITestSetup_Large", "l/", logger, permissionHandler));

		WebDriver webDriver = new WebDriver(new WebDriverConfig(
				map,
				8080, 0, 0, "com/greenjon902/hisdoc/logo.ico"
		), logger, sessionHandler, permissionHandler);
		webDriver.start();
	}

	/**
	 * Creates a new database with the given name, and fills it with information from the given sql file.
	 * Then uses this to create a map from paths to page renderers.
	 *
	 * @param dbName            The name of the database to create
	 * @param sqlScriptName     The name of the sql script that fills the database with test data
	 * @param pageNamePrefix    The text to prefix page names with, can be "", or could be "test/"
	 * @param permissionHandler The permission handler to supply to pages which require it
	 */
	private static Map<String, PageRenderer> createTheThing(DB database, String dbName, String sqlScriptName, String pageNamePrefix, Logger logger, PermissionHandler permissionHandler) throws ManagedProcessException, SQLException {
		database.createDB(dbName);
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName + "?allowMultiQueries=true");

		Dispatcher dispatcher = new Dispatcher(connection, logger, new TestMinecraftInfoSupplierImpl());
		dispatcher.createTables();
		dispatcher.prepare(sqlScriptName).execute();  // Fill with test data
		
		return Map.ofEntries(Map.entry("/" + pageNamePrefix, new HomePageRenderer()),
				Map.entry("/" + pageNamePrefix + "event", new EventPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tag", new TagPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tags", new TagsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "person", new PersonPageRenderer(dispatcher, new TestMcPlaytimeSupplierImpl())),
				Map.entry("/" + pageNamePrefix + "persons", new PersonsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "timeline", new TimelinePageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "add", new AddEventPageRenderer(dispatcher, permissionHandler)),
				Map.entry("/" + pageNamePrefix + "addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, permissionHandler)),
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
	private final Map<UUID, @NotNull Integer> sessionToPidMap;

	TestSessionHandlerImpl(Map<UUID, @NotNull Integer> sessionToPidMap) {
		this.sessionToPidMap = sessionToPidMap;
	}

	@Override
	public int getPid(UUID sessionId) {
		return sessionToPidMap.getOrDefault(sessionId, 0);
	}
}

class TestPermissionHandlerImpl implements PermissionHandler {
	private final Map<@NotNull Integer, Set<Permission>> pidToPermissionMap;

	TestPermissionHandlerImpl(Map<@NotNull Integer, Set<Permission>> pidToPermissionMap) {
		this.pidToPermissionMap = pidToPermissionMap;
	}

	@Override
	public boolean hasPermission(int pid, Permission permission) {
		return pidToPermissionMap.getOrDefault(pid, Collections.emptySet()).contains(permission);
	}
}

class TestMinecraftInfoSupplierImpl implements MinecraftInfoSupplier {
	@Override
	public int getTicks(UUID uuid) {
		return 0;
	}

	@Override
	public String getUsername(UUID uuid) {
		System.out.println("Loading player name for " + uuid + "...");
		PlayerProfile playerProfile = new Mojang().connect().getPlayerProfile(uuid.toString());

		String username = playerProfile.getUsername();
		System.out.println("Got " + username);
		return username;
	}
}