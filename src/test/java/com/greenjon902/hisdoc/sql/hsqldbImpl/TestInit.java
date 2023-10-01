package com.greenjon902.hisdoc.sql.hsqldbImpl;

import com.greenjon902.hisdoc.sql.hsqldbImpl.hsqldbImpl.HSQLDBDispatcherImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import static com.greenjon902.hisdoc.sql.hsqldbImpl.Utils.getColumnValues;
import static com.greenjon902.hisdoc.sql.hsqldbImpl.Utils.makeInMemoryConnection;

public class TestInit {
	/**
	 * Executes a sql from a code for this specific test. It appends the type to the start of the code.
	 * See {@link Utils#execute(Connection, String)}
	 */
	private static ResultSet execute(Connection conn, String code) throws SQLException, IOException {
		return Utils.execute(conn, "init/" + code);
	}

	@Test
	public void should_createTables_when_noneExist() throws ClassNotFoundException, SQLException, IOException {
		Connection conn = makeInMemoryConnection();

		HSQLDBDispatcherImpl dispatcher = new HSQLDBDispatcherImpl(conn);
		dispatcher.dispatchInit();

		Set<String> names = getColumnValues(execute(conn, "getTableNames"), "TABLE_NAME", String::toLowerCase);
		Assertions.assertEquals(
				Set.of("users", "dates", "events", "eventuserrelations", "eventtagrelations", "tags", "changelog", "eventeventrelations"),
				names
		);
	}

	@Test
	public void should_createTables_when_someExist() throws ClassNotFoundException, SQLException, IOException {
		Connection conn = makeInMemoryConnection();

		execute(conn, "createTestTables");
		execute(conn, "fillTestTables");

		HSQLDBDispatcherImpl dispatcher = new HSQLDBDispatcherImpl(conn);
		dispatcher.dispatchInit();

		Set<String> names = getColumnValues(execute(conn, "getTableNames"), "TABLE_NAME", String::toLowerCase);
		Assertions.assertEquals(
				Set.of("users", "dates", "events", "eventuserrelations", "eventtagrelations", "tags", "changelog", "eventeventrelations"),
				names
		);

		// Check old values are still there
		Set<String> values = getColumnValues(execute(conn, "getTestValues"), "text");
		Assertions.assertEquals(
				Set.of("bar", "foo", "baz", "testingInfo"),
				values
		);

	}
}