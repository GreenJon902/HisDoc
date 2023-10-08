package com.greenjon902.hisdoc;

import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.pages.EventPageRenderer;
import com.greenjon902.hisdoc.pages.TagPageRenderer;
import com.greenjon902.hisdoc.pages.UserPageRenderer;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class UITest {
	public static void main(String[] args) throws Exception {
		DB database = DB.newEmbeddedDB(3306);
		database.start();

		String name = "HisDocUITest";  // So each test uses the same mysqlDB but can have its own database
		database.createDB(name);
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/" + name + "?allowMultiQueries=true");

		Dispatcher dispatcher = new Dispatcher(connection);
		dispatcher.createTables();
		dispatcher.prepare("UITestSetup").execute();  // Make a test database

		WebDriver webDriver = new WebDriver(new WebDriverConfig(
				Map.of("/event", new EventPageRenderer(dispatcher),
						"/tag", new TagPageRenderer(dispatcher),
						"/user", new UserPageRenderer(dispatcher)),
				8080, 0, 0
		));
		webDriver.start();
	}
}
