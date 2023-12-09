package com.greenjon902.hisdoc.runners.papermc;

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
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.sessionHandler.SessionHandler.VerifyResult.*;

public class HisDocRunner extends JavaPlugin {
	private ConfigLoader configLoader;


	@Override
	public void onEnable() {
		configLoader = new ConfigLoader(getDataFolder());

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	private void makeWebDriver() throws SQLException, IOException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName + "?allowMultiQueries=true");

		Dispatcher dispatcher = new Dispatcher(connection);
		dispatcher.createTables();

		WebDriver webDriver = new WebDriver(new WebDriverConfig(
				Map.ofEntries(Map.entry("/", new HomePageRenderer()),
						Map.entry("/event", new EventPageRenderer(dispatcher)),
						Map.entry("/tag", new TagPageRenderer(dispatcher)),
						Map.entry("/tags", new TagsPageRenderer(dispatcher)),
						Map.entry("/person", new PersonPageRenderer(dispatcher)),
						Map.entry("/persons", new PersonsPageRenderer(dispatcher)),
						Map.entry("/timeline", new TimelinePageRenderer(dispatcher)),
						Map.entry("/addS", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(NO_SESSION))),
						Map.entry("/addI", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(INVALID_IP))),
						Map.entry("/addV", new AddEventPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID))),
						Map.entry("/addEventSubmit", new AddEventSubmitPageRenderer(dispatcher, new TestSessionHandlerImpl(VALID))),
						Map.entry("/add", new PageRenderer() {  // A helper page for choosing what to happen on adding
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
						Map.entry("/themes", new CssPageRenderer())),
				8080, 0, 0, "com/greenjon902/hisdoc/logo.ico"
		));
		webDriver.start();
	}
}

