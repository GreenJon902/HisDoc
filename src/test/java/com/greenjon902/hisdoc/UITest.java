package com.greenjon902.hisdoc;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.pages.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;
import org.jetbrains.annotations.NotNull;
import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import static com.greenjon902.hisdoc.Utils.getTestLogger;

public class UITest {

	public static void main(String[] args) throws Exception {
		Logger logger = getTestLogger();

		DB database = DB.newEmbeddedDB(3306);
		database.start();

		SessionHandler sessionHandler = new TestSessionHandlerImpl(Map.of(
				UUID.fromString("0000000-0000-0000-0000-000000000001"), 1
		));
		PermissionHandler permissionHandler = new TestPermissionHandlerImpl(Map.of(
				0, Set.of(Permission.LOAD_PAGE),
				1, Set.of(Permission.LOAD_PAGE, Permission.ADD_EVENT)
		));

		HashMap<String, PageRenderer> map = new HashMap<>();
		map.putAll(createTheThing(database, "HisDocUITest_Empty", "UITestSetup_Empty", "e/", logger, permissionHandler, sessionHandler));
		map.putAll(createTheThing(database, "HisDocUITest_Refined", "UITestSetup_Refined", "", logger, permissionHandler, sessionHandler));
		map.putAll(createTheThing(database, "HisDocUITest_Large", "UITestSetup_Large", "l/", logger, permissionHandler, sessionHandler));

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
	private static Map<String, PageRenderer> createTheThing(DB database, String dbName, String sqlScriptName, String pageNamePrefix, Logger logger, PermissionHandler permissionHandler, SessionHandler sessionHandler) throws ManagedProcessException, SQLException {
		database.createDB(dbName);
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName + "?allowMultiQueries=true");

		Dispatcher dispatcher = new Dispatcher(connection, logger, new TestMinecraftInfoSupplierImpl());
		dispatcher.createTables();
		dispatcher.prepare(sqlScriptName).execute();  // Fill with test data
		
		return Map.ofEntries(Map.entry("/" + pageNamePrefix, new HomePageRenderer()),
				Map.entry("/" + pageNamePrefix + "event", new EventPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tag", new TagPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "tags", new TagsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "person", new PersonPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "persons", new PersonsPageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "timeline", new TimelinePageRenderer(dispatcher)),
				Map.entry("/" + pageNamePrefix + "add", new AddEventPageRenderer(dispatcher, permissionHandler, sessionHandler)),
				Map.entry("/" + pageNamePrefix + "addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, permissionHandler)),
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
		if (sessionId == null) return 0;
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