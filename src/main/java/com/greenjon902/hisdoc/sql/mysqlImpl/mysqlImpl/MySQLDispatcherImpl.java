package com.greenjon902.hisdoc.sql.mysqlImpl.mysqlImpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLDispatcherImpl {
	private final Connection conn;

	public MySQLDispatcherImpl(Connection conn) {
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
	}

	public void dispatchGetEvent(int eid) {
		try (CallableStatement cs = conn.prepareCall("CALL getEvent(?)}")) {
			cs.setInt("eid", eid);

			cs.execute();
			System.out.println(cs.getString("name"));

			/*EventInfo event = new EventInfo(
					result.getInt("eid"),
					result.getString("name"),
					result.getString("desc"),
					(String[]) result.getArray("TagName").getArray(),
					(int[]) result.getArray("TagColor").getArray(),
					(String[]) result.getArray("UserInfo").getArray(),
					(String[]) result.getArray("ChangeDesc").getArray(),
					(String[]) result.getArray("ChangeAuthorInfo").getArray(),
					(String[]) result.getArray("ChangeDate").getArray(),
					(String[]) result.getArray("DateType").getArray(),
					(String[]) result.getArray("Date1").getArray(),
					(String[]) result.getArray("DatePrecision").getArray(),
					(String[]) result.getArray("DateDiff").getArray(),
					(String[]) result.getArray("DateDiffType").getArray(),
					(String[]) result.getArray("Date2").getArray()
			);

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
			}*/

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
