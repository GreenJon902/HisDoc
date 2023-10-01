package com.greenjon902.hisdoc.sql.hsqldbImpl;


import com.greenjon902.hisdoc.sql.EventInfo;
import com.greenjon902.hisdoc.sql.SQLManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HSQLDBManagerImpl implements SQLManager {
	Connection conn;
	HSQLDBDispatcherImpl dispatcher;

	public HSQLDBManagerImpl(String path) throws SQLException {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
			return;
		}

		conn = DriverManager.getConnection(path);
		dispatcher = new HSQLDBDispatcherImpl(conn);
		dispatcher.dispatchInit();
		conn.close();
	}


	@Override
	public EventInfo getEvent(int id) {
		return null;
	}
}
