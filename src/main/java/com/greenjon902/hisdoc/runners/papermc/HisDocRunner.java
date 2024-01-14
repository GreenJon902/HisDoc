package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.pages.*;
import com.greenjon902.hisdoc.pages.eventModification.AddEventPageRenderer;
import com.greenjon902.hisdoc.pages.eventModification.AddEventSubmitPageRenderer;
import com.greenjon902.hisdoc.runners.papermc.command.CommandHandler;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.greenjon902.hisdoc.runners.papermc.ConfigLoader.ConfigItem.*;

// TODO: Toggle for verbose logging
// TODO: Toggle for HisDoc only log files
// TODO: Custom HisDoc log path

public class HisDocRunner extends JavaPlugin {
	private ConfigLoader configLoader;
	private WebDriver webDriver;
	private Dispatcher dispatcher;
	private Connection connection;
	private PaperMcSessionHandlerImpl sessionHandler;
	private PaperMcPermissionHandlerImpl permissionHandler;
	private Logger logger;


	@Override
	public void onEnable() {
		try {
			// Setup the logger
			if (logger == null) {  // Let logger persist as it should not have issues like ever. Also, then it might get multiple file handlers!
				logger = getLogger();
				logger.setLevel(Level.ALL);
				logger.addHandler(makeLoggerFileHandler());
			}
			logger.info("Starting HisDoc...");

			// Load config -----------------------
			logger.fine("Loading config...");
			configLoader = new ConfigLoader(getDataFolder(), logger);
			String mysqlHost = configLoader.get(MYSQL_HOST);
			String mysqlUser = configLoader.get(MYSQL_USER);
			String mysqlPassword = configLoader.get(MYSQL_PASSWORD);
			int webDriverPort = Integer.parseInt(configLoader.get(WEBDRIVER_PORT));

			// Create sql connection -----------------------
			String url = "jdbc:mysql://" + mysqlHost + "?allowMultiQueries=true&autoReconnect=true";
			logger.fine("Connecting to database - \"" + url + "\"");
			connection = DriverManager.getConnection(url, mysqlUser, mysqlPassword);

			logger.fine("Connected to " + connection);
			dispatcher = new Dispatcher(connection, logger, new PaperMinecraftInfoSupplierImpl(logger));
			dispatcher.createTables();

			// Set session tracking and permissions -----------------------
			logger.fine("Setting up session tracking and permissions...");
			sessionHandler = new PaperMcSessionHandlerImpl(logger);
			permissionHandler = new PaperMcPermissionHandlerImpl(dispatcher);

			// Set up commands -----------------------
			logger.fine("Setting up commands...");
			getCommand("hisdoc").setExecutor(new CommandHandler(dispatcher, sessionHandler, logger, this));

			// Set up website stuffs -----------------------
			logger.fine("Starting webdriver...");
			Map<String, PageRenderer> map = createMap(dispatcher, sessionHandler, permissionHandler);
			webDriver = new WebDriver(new WebDriverConfig(
					map,
					webDriverPort, 0, 0, "com/greenjon902/hisdoc/logo.ico"
			), logger,sessionHandler, permissionHandler);

			webDriver.start();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDisable() {
		logger.info("Stopping HisDoc...");
		Exception exception = null;

		PluginCommand command;
		if ((command = getCommand("addevent")) != null) {
			command.setExecutor(null);
		}
		if ((command = getCommand("restarthisdoc")) != null) {
			command.setExecutor(null);
		}
		webDriver.stop();
		webDriver = null;
		dispatcher = null;
		sessionHandler = null;
		permissionHandler = null;
		try {
			connection.close();
		} catch (SQLException e) {
			exception = e;
		}
		connection = null;
		configLoader = null;

		if (exception != null) {
			throw new RuntimeException(exception);
		}
	}

	private static Map<String, PageRenderer> createMap(Dispatcher dispatcher, SessionHandler sessionHandler, PermissionHandler permissionHandler) {
		return Map.ofEntries(Map.entry("/", new HomePageRenderer()),
			Map.entry("/event", new EventPageRenderer(dispatcher)),
			Map.entry("/tag", new TagPageRenderer(dispatcher)),
			Map.entry("/tags", new TagsPageRenderer(dispatcher)),
			Map.entry("/person", new PersonPageRenderer(dispatcher)),
			Map.entry("/persons", new PersonsPageRenderer(dispatcher)),
			Map.entry("/timeline", new TimelinePageRenderer(dispatcher)),
			Map.entry("/add", new AddEventPageRenderer(dispatcher, permissionHandler, sessionHandler)),
			Map.entry("/addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, permissionHandler)),
			Map.entry("/themes", new CssPageRenderer()));
	}

	private FileHandler makeLoggerFileHandler() {
		try {
			File logFolder = new File(Bukkit.getPluginsFolder().getParentFile(), "logs");
			if (!logFolder.exists()) {
				throw new RuntimeException("HisDoc cannot find log folder! Tried to use " + logFolder);
			}
			String date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			FileHandler fh = new FileHandler(logFolder.getAbsolutePath() + "/HisDoc-" + date + ".log", true);
			LogFormatter formatter = new LogFormatter();
			fh.setFormatter(formatter);
			fh.setLevel(Level.ALL);
			return fh;
		} catch (SecurityException | IOException e) {
			throw new RuntimeException(e);
		}

	}
}

