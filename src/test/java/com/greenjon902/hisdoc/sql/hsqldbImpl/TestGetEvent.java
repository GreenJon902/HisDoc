package com.greenjon902.hisdoc.sql.hsqldbImpl;

import com.greenjon902.hisdoc.sql.hsqldbImpl.hsqldbImpl.HSQLDBDispatcherImpl;
import com.greenjon902.hisdoc.sql.hsqldbImpl.hsqldbImpl.HSQLDBManagerImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.greenjon902.hisdoc.sql.hsqldbImpl.Utils.makeInMemoryConnection;

public class TestGetEvent {
	/**
	 * Executes a sql from a code for this specific test. It appends the type to the start of the code.
	 * See {@link Utils#execute(Connection, String)}
	 */
	private static ResultSet execute(Connection conn, String code) throws SQLException, IOException {
		return Utils.execute(conn, "getEvent/" + code);
	}

	@Test
	public void should_returnNoEvents_when_noEventsExist() throws SQLException, ClassNotFoundException {
		Connection conn = null;
		try {
			conn = makeInMemoryConnection();

			HSQLDBDispatcherImpl dispatcher = new HSQLDBDispatcherImpl(conn);
			HSQLDBManagerImpl.loadProcedures(conn);
			dispatcher.dispatchInit();
			dispatcher.dispatchGetEvent(0);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}
}
