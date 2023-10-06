package com.greenjon902.hisdoc.sql.hsqldbImpl.hsqldbImpl;


import com.greenjon902.hisdoc.sql.hsqldbImpl.EventInfo;
import com.greenjon902.hisdoc.sql.hsqldbImpl.SQLManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

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
		loadProcedures(conn);
		dispatcher = new HSQLDBDispatcherImpl(conn);
		dispatcher.dispatchInit();
		conn.close();
	}

	public static void loadProcedures(Connection conn) {
		URL packageURL;
		packageURL = conn.getClass().getClassLoader().getResource("com/greenjon902/hisdoc/sql/hsqldbImpl/procedures/");
		Objects.requireNonNull(packageURL, "Could not find procedures folder");
		try {
			String[] files = new File(packageURL.toURI()).list();
			Arrays.sort(files);
			Objects.requireNonNull(files, "Failed to get script list");
			for (String file : files) {
				loadScript("com/greenjon902/hisdoc/sql/hsqldbImpl/procedures/" + file, conn);
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static void loadScript(String path, Connection conn) {
		System.out.println("Loading script at \"" + path + "\"");
		String string;
		try {
			InputStream fileInputStream = conn.getClass().getClassLoader().getResourceAsStream(path);
			string = new String(fileInputStream.readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			conn.createStatement().execute(string);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public EventInfo getEvent(int id) {
		return null;
	}
}
