package com.greenjon902.hisdoc.sql.hsqldbImpl.hsqldbImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class HSQLDBDispatcherImpl {
	private final Connection conn;

	public HSQLDBDispatcherImpl(Connection conn) {
		this.conn = conn;
	}

	private void dispatch(String string) {
		try {
			conn.createStatement().execute(string);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void dispatchInit() {
		String string;
		try {
			InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/sql/hsqldbImpl/init.sql");
			string = new String(fileInputStream.readAllBytes());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		dispatch(string);
	}
}
