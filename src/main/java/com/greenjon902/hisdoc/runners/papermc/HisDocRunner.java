package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.pages.*;
import com.greenjon902.hisdoc.runners.papermc.command.AddEventCommand;
import com.greenjon902.hisdoc.runners.papermc.command.RestartHisDocCommand;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
			String addEventUrl = configLoader.get(ADD_EVENT_URL);
			int webDriverPort = Integer.parseInt(configLoader.get(WEBDRIVER_PORT));

			// Create sql connection -----------------------
			String url = "jdbc:mysql://" + mysqlHost + "?allowMultiQueries=true&autoReconnect=true";
			logger.fine("Connecting to database - \"" + url + "\"");
			connection = DriverManager.getConnection(url, mysqlUser, mysqlPassword);

			logger.fine("Connected to " + connection);
			dispatcher = new Dispatcher(connection, logger);
			dispatcher.createTables();

			// Set up commands -----------------------
			logger.fine("Setting up commands...");
			sessionHandler = new PaperMcSessionHandlerImpl(logger, addEventUrl);
			getCommand("addevent").setExecutor(new AddEventCommand(dispatcher, sessionHandler, addEventUrl, logger));
			getCommand("restarthisdoc").setExecutor(new RestartHisDocCommand(this, logger));

			// Set up website stuffs -----------------------
			logger.fine("Starting webdriver...");
			Map<String, PageRenderer> map = createMap(dispatcher, sessionHandler, getDataFolder());
			webDriver = new WebDriver(new WebDriverConfig(
					map,
					webDriverPort, 0, 0, "com/greenjon902/hisdoc/logo.ico"
			), logger);

			webDriver.start();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}

		BukkitScheduler scheduler = this.getServer().getScheduler();
		// FIXME: Find a better way to load the cache
		scheduler.runTaskAsynchronously(this, () -> {
			try {
				// Schedule this async to not delay loading times.

				// Render the persons page as most of the names we will need will be cached by that
				new PersonsPageRenderer(dispatcher).render(Collections.emptyMap(), null, User.empty());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
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

	private static Map<String, PageRenderer> createMap(Dispatcher dispatcher, SessionHandler sessionHandler, File dataFolder) {
		return Map.ofEntries(Map.entry("/hs/", new HomePageRenderer()),
			Map.entry("/hs/event", new EventPageRenderer(dispatcher)),
			Map.entry("/hs/tag", new TagPageRenderer(dispatcher)),
			Map.entry("/hs/tags", new TagsPageRenderer(dispatcher)),
			Map.entry("/hs/person", new PersonPageRenderer(dispatcher, new PaperMcPlaytimeSupplierImpl())),
			Map.entry("/hs/persons", new PersonsPageRenderer(dispatcher)),
			Map.entry("/hs/timeline", new TimelinePageRenderer(dispatcher)),
			Map.entry("/hs/add", new AddEventPageRenderer(dispatcher, sessionHandler, true)),
			Map.entry("/hs/addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, sessionHandler)),
			Map.entry("/hs/themes", new CssPageRenderer()));
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

