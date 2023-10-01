package com.greenjon902.hisdoc.sql.hsqldbImpl;

import com.greenjon902.hisdoc.sql.hsqldbImpl.hsqldbImpl.HSQLDBDispatcherImpl;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static com.greenjon902.hisdoc.sql.hsqldbImpl.Utils.makeInMemoryConnection;

public class TestGetEvent {
	@Test
	public void test() throws SQLException, ClassNotFoundException {
		Connection conn = null;
		try {
			conn = makeInMemoryConnection();

			HSQLDBDispatcherImpl dispatcher = new HSQLDBDispatcherImpl(conn);
			dispatcher.dispatchInit();
			dispatcher.dispatchGetEvent(0);
		} finally {
			//conn.close();
		}

	}
}
