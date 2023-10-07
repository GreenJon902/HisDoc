package com.greenjon902.hisdoc.sql;

import ch.vorburger.exec.ManagedProcessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import static com.greenjon902.hisdoc.sql.Utils.*;

// TODO: This could be loaded from a list of scripts to run, then a queries and expected results

public class TestInit {
	Set<String> tableNames = Set.of("hs_user", "hs_event", "hs_eventuserrelation", "hs_eventtagrelation", "hs_tag", "hs_changelog", "hs_eventeventrelation");

	@Test
	public void should_createTables_when_noneExist() throws ClassNotFoundException, SQLException, IOException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		Set<String> names = getColumnValues(easyDispatch(dispatcher, "testInit/getTableNames"), "TABLE_NAME", String::toLowerCase);

		Assertions.assertEquals(
				tableNames,
				names
		);
	}

	@Test
	public void should_createTables_when_someExist() throws ClassNotFoundException, SQLException, IOException {
		Connection conn = makeInMemoryConnection();

		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.prepareWithArgs("createTables/user").execute();
		dispatcher.prepareWithArgs("createTables/tag").execute();
		dispatcher.prepareWithArgs("testInit/fillTag").execute();
		dispatcher.prepareWithArgs("testInit/fillUser").execute();
		dispatcher.createTables();

		dispatcher.createTables();

		Set<String> names = getColumnValues(easyDispatch(dispatcher, "testInit/getTableNames"), "TABLE_NAME", String::toLowerCase);
		Assertions.assertEquals(
				tableNames,
				names
		);

		// Check old values are still there
		Set<String> values = getColumnValues(easyDispatch(dispatcher, "testInit/getTestValues"), "text");
		Assertions.assertEquals(
				Set.of("bar", "foo", "baz", "testingInfo"),
				values
		);

	}
}