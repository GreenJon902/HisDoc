package com.greenjon902.hisdoc.sql.hsqldbImpl.hsqldbImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

	public void dispatchGetEvent(int eid) {
		String string;
		try {
			InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/sql/hsqldbImpl/getEvent.sql");
			string = new String(fileInputStream.readAllBytes());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			ResultSet result = conn.createStatement().executeQuery(string.replace("{0}", String.valueOf(eid)));

			while(result.next()){

				System.out.println(padLeftZeros(result.getMetaData().getColumnName(1) , 15)+ ": " + result.getInt(1));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(2) , 15)+ ": " + result.getString(2));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(3) , 15)+ ": " + result.getString(3));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(4) , 15)+ ": " + Arrays.toString((Object[]) result.getArray(4).getArray()));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(5) , 15)+ ": " + Arrays.toString((Object[]) result.getArray(5).getArray()));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(6) , 15)+ ": " + Arrays.toString((Object[]) result.getArray(6).getArray()));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(7) , 15)+ ": " + Arrays.toString((Object[]) result.getArray(7).getArray()));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(8) , 15)+ ": " + Arrays.toString((Object[]) result.getArray(8).getArray()));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(9) , 15)+ ": " + Arrays.toString((Object[]) result.getArray(9).getArray()));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(10), 15) + ": " + result.getString(10));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(11), 15) + ": " + result.getString(11));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(12), 15) + ": " + result.getString(12));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(13), 15) + ": " + result.getString(13));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(14), 15) + ": " + result.getString(14));
				System.out.println(padLeftZeros(result.getMetaData().getColumnName(15), 15) + ": " + result.getString(15));
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String padLeftZeros(String inputString, int length) {
		if (inputString.length() >= length) {
			return inputString;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(inputString);
		while (sb.length() < length) {
			sb.append(' ');
		}


		return sb.toString();
	}
}
