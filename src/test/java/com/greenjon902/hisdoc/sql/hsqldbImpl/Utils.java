package com.greenjon902.hisdoc.sql.hsqldbImpl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Utils {
	/**
	 * Execute a sql script at the location where they are stored
	 * @param conn The connection
	 * @param code The script code (the path)
	 * @return The result
	 */
	public static ResultSet execute(Connection conn, String code) throws SQLException, IOException {
		Statement stmt = conn.createStatement();

		String string;
		try (InputStream stream = Utils.class.getClassLoader().getResourceAsStream(
				"com/greenjon902/hisdoc/sql/hsqldbImpl/" + code + ".sql")) {
			if (stream == null) throw new RuntimeException("Could not find sql util \"" + code + "\"");
			string = new String(stream.readAllBytes());
		}

		return stmt.executeQuery(string);
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

	public static Connection makeInMemoryConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		return DriverManager.getConnection("jdbc:hsqldb:mem");
	}
}
