package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.hsqldbImpl.HSQLDBDispatcherImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

public class HSQLDBDispatcherImplTest {
	private static Connection makeInMemoryConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		return DriverManager.getConnection("jdbc:hsqldb:mem");
	}

	@Test
	public void should_createTables_when_noneExist() throws ClassNotFoundException, SQLException, IOException {
		Connection conn = makeInMemoryConnection();

		HSQLDBDispatcherImpl dispatcher = new HSQLDBDispatcherImpl(conn);
		dispatcher.dispatchInit();

		Assertions.assertEquals(
				HSQLDBTestUtils.getLowerTableNames(conn),
				Set.of("users", "dates", "events", "eventuserrelations", "eventtagrelations", "tags", "changelog", "eventeventrelations")
		);
	}

	@Test
	public void should_createTables_when_someExist() throws ClassNotFoundException, SQLException, IOException {
		Connection conn = makeInMemoryConnection();

		HSQLDBTestUtils.createFillTestTables(conn);

		HSQLDBDispatcherImpl dispatcher = new HSQLDBDispatcherImpl(conn);
		dispatcher.dispatchInit();

		Assertions.assertEquals(
				HSQLDBTestUtils.getLowerTableNames(conn),
				Set.of("users", "dates", "events", "eventuserrelations", "eventtagrelations", "tags", "changelog", "eventeventrelations")
		);

		// Check old values are still there
		Assertions.assertEquals(
				HSQLDBTestUtils.getSomeTestValues(conn),
				Set.of("bar", "foo", "baz", "testinginfo")
		);

	}
}