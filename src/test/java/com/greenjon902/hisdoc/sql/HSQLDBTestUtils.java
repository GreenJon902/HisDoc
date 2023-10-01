package com.greenjon902.hisdoc.sql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HSQLDBTestUtils {
	private static ResultSet execute(Connection conn, String code) throws SQLException, IOException {
		Statement stmt = conn.createStatement();

		String string;
		try (InputStream stream = HSQLDBTestUtils.class.getClassLoader().getResourceAsStream(
				"com/greenjon902/hisdoc/sql/utils/" + code + ".sql")) {
			if (stream == null) throw new RuntimeException("Could not find sql util \"" + code + "\"");
			string = new String(stream.readAllBytes());
		}

		return stmt.executeQuery(string);
	}

	private static <T> Set<T> getColumnValues(ResultSet result, String columnName, Function<T, T> converter) throws SQLException {
		Set<T> names = new HashSet<>();
		while(result.next()){
			names.add(converter.apply((T) result.getObject(columnName)));
		}
		return names;
	}

	public static Set<String> getLowerTableNames(Connection conn) throws SQLException, IOException {
		ResultSet result = execute(conn, "getTableNames");
		return getColumnValues(result, "TABLE_NAME", String::toLowerCase);
	}

	public static Set<String> getSomeTestValues(Connection conn) throws SQLException, IOException {
		ResultSet result = execute(conn, "getSomeTestValues");
		return getColumnValues(result, "text", String::toLowerCase);
	}

	public static void createFillTestTables(Connection conn) throws SQLException, IOException {
		execute(conn, "createSomeTestTables");
		execute(conn, "fillSomeTestTables");
	}
}
