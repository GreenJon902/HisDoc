package com.greenjon902.hisdoc;

import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.pages.EventPage;
import com.greenjon902.hisdoc.sql.Dispatcher;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

		FileOutputStream f = new FileOutputStream("./test.html");
		f.write(new EventPage(dispatcher).render(null, Map.of("id", "1"), null).getBytes());
		f.close();

		database.stop();
	}
}
