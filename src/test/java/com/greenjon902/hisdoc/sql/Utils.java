package com.greenjon902.hisdoc.sql;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.sql.Dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class Utils {
	private static DB database;  // Store this so we don't make one for every test
	private static int dbAmount = 0;

	/**
	 * Execute a sql script at the location where they are stored
	 * @param code The script code (the path)
	 * @return The result
	 */
	public static ResultSet easyDispatch(Dispatcher dispatcher, String code) throws SQLException, IOException {
		PreparedStatement ps = dispatcher.prepare(code);
		ps.execute();
		return ps.getResultSet();
	}

	public static <T> Set<T> getColumnValues(ResultSet result, String columnName, Function<T, T> converter) throws SQLException {
		Set<T> names = new HashSet<>();
		while(result.next()){
			names.add(converter.apply((T) result.getObject(columnName)));
		}
		return names;
	}

	public static <T> Set<T> getColumnValues(ResultSet result, String columnName) throws SQLException {
		return getColumnValues(result, columnName, Function.identity());
	}

	public static Connection makeInMemoryConnection() throws SQLException {
		try {
			if (database == null) {
				database = DB.newEmbeddedDB(3306);
				database.start();
			}

			String name = "HisDocTest" + dbAmount;  // So each test uses the same mysqlDB but can have its own database
			database.createDB(name);
			dbAmount += 1;
			return DriverManager.getConnection("jdbc:mysql://localhost/" + name + "?allowMultiQueries=true");

		} catch (ManagedProcessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void waitForNewline() {  // Can be used during testing to keep the database open
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}
}
