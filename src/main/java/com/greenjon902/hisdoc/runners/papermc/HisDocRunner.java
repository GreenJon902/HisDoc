package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.pages.*;
import com.greenjon902.hisdoc.runners.papermc.command.AddEventCommand;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.runners.papermc.ConfigLoader.ConfigItem.*;

public class HisDocRunner extends JavaPlugin {
	private ConfigLoader configLoader;
	private WebDriver webDriver;
	private Dispatcher dispatcher;
	private Connection connection;
	private PaperMcSessionHandlerImpl sessionHandler;


	@Override
	public void onEnable() {
		try {
			// Load config -----------------------
			configLoader = new ConfigLoader(getDataFolder());
			String mysqlHost = configLoader.get(MYSQL_HOST);
			String mysqlUser = configLoader.get(MYSQL_USER);
			String mysqlPassword = configLoader.get(MYSQL_PASSWORD);
			String addEventUrl = configLoader.get(ADD_EVENT_URL);
			int webDriverPort = Integer.parseInt(configLoader.get(WEBDRIVER_PORT).strip());

			// Create sql connection -----------------------

			System.out.println(Class.forName("com.mysql.cj.jdbc.Driver"));
			connection = DriverManager.getConnection("jdbc:mysql://" + mysqlHost.strip() + "?allowMultiQueries=true",
					mysqlUser.strip(), mysqlPassword.strip());

			System.out.println("Connected to " + connection);
			dispatcher = new Dispatcher(connection);

			// Set up commands -----------------------
			sessionHandler = new PaperMcSessionHandlerImpl();
			getCommand("addevent").setExecutor(new AddEventCommand(dispatcher, sessionHandler, addEventUrl));

			// Set up website stuffs -----------------------
			Map<String, PageRenderer> map = createMap(dispatcher, sessionHandler);
			webDriver = new WebDriver(new WebDriverConfig(
					map,
					webDriverPort, 0, 0, "com/greenjon902/hisdoc/logo.ico"
			));

			webDriver.start();
		} catch (SQLException | IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDisable() {
		try {
			getCommand("addevent").setExecutor(null);
			webDriver.stop();
			webDriver = null;
			dispatcher = null;
			connection.close();
			connection = null;
			configLoader = null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, PageRenderer> createMap(Dispatcher dispatcher, SessionHandler sessionHandler) {
		return Map.ofEntries(Map.entry("/", new HomePageRenderer()),
			Map.entry("/event", new EventPageRenderer(dispatcher)),
			Map.entry("/tag", new TagPageRenderer(dispatcher)),
			Map.entry("/tags", new TagsPageRenderer(dispatcher)),
			Map.entry("/person", new PersonPageRenderer(dispatcher)),
			Map.entry("/persons", new PersonsPageRenderer(dispatcher)),
			Map.entry("/timeline", new TimelinePageRenderer(dispatcher)),
			Map.entry("/add", new AddEventPageRenderer(dispatcher, sessionHandler, true)),
			Map.entry("/addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, sessionHandler)),
			Map.entry("/themes", new CssPageRenderer()));
	}
}

