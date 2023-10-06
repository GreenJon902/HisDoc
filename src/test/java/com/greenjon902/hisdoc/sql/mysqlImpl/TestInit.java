package com.greenjon902.hisdoc.sql.mysqlImpl;

import ch.vorburger.exec.ManagedProcessException;
import com.greenjon902.hisdoc.sql.mysqlImpl.mysqlImpl.MySQLDispatcherImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import static com.greenjon902.hisdoc.sql.mysqlImpl.Utils.getColumnValues;
import static com.greenjon902.hisdoc.sql.mysqlImpl.Utils.makeInMemoryConnection;

// TODO: This could be loaded from a list of scripts to run, then a queries and expected results

public class TestInit {
	/**
	 * Executes a sql from a code for this specific test. It appends the type to the start of the code.
	 * See {@link Utils#execute(Connection, String)}
	 */
	private static ResultSet execute(Connection conn, String code) throws SQLException, IOException {
		return Utils.execute(conn, "init/" + code);
	}

	@Test
	public void should_createTables_when_noneExist() throws ClassNotFoundException, SQLException, IOException, ManagedProcessException {
		Connection conn = makeInMemoryConnection();

		MySQLDispatcherImpl dispatcher = new MySQLDispatcherImpl(conn);
		dispatcher.dispatchInit();

		Set<String> names = getColumnValues(execute(conn, "getTableNames"), "TABLE_NAME", String::toLowerCase);
		Assertions.assertEquals(
				Set.of("user", "date", "event", "eventuserrelation", "eventtagrelation", "tag", "changelog", "eventeventrelation"),
				names
		);
	}

	@Test
	public void should_createTables_when_someExist() throws ClassNotFoundException, SQLException, IOException, ManagedProcessException {
		Connection conn = makeInMemoryConnection();

		execute(conn, "createTestTables");
		execute(conn, "fillTestTables");

		MySQLDispatcherImpl dispatcher = new MySQLDispatcherImpl(conn);
		dispatcher.dispatchInit();

		Set<String> names = getColumnValues(execute(conn, "getTableNames"), "TABLE_NAME", String::toLowerCase);
		Assertions.assertEquals(
				Set.of("user", "date", "event", "eventuserrelation", "eventtagrelation", "tag", "changelog", "eventeventrelation"),
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