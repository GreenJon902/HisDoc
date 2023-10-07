package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.ChangeInfo;
import com.greenjon902.hisdoc.sql.results.DateInfo;
import com.greenjon902.hisdoc.sql.results.EventInfo;
import com.greenjon902.hisdoc.sql.results.TagInfo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
	Map<String, String> statements = new HashMap<>();
	Connection conn;

	public Dispatcher(Connection conn) {
		this.conn = conn;
	}

	protected PreparedStatement prepare(String... codes) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(checkStatement("statement/start"));
		for (int i=0; i<codes.length; i++) {
			sql.append(checkStatement(codes[i]));
			if (i != codes.length - 1) {
				sql.append(checkStatement("statement/delimiter"));
			}
		}
		sql.append(checkStatement("statement/end"));
		return conn.prepareStatement(sql.toString());
	}

	protected PreparedStatement prepareWithArgs(String code, Object... arguments) throws SQLException {
		PreparedStatement ps = prepare(code);

		for (int i=0; i < arguments.length / 2; i++) {
			ps.setObject(i + 1, arguments[i], (Integer) arguments[i + 1]);
		}

		return ps;
	}

	private String checkStatement(String code) {
		if (!statements.containsKey(code)) {
			try {
				System.out.println("Loading \"" + code + "\" ----------------");
				InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/sql/statements/" + code + ".sql");
				statements.put(code, new String(fileInputStream.readAllBytes()).replace("{prefix}", "hs_"));
				System.out.println(statements.get(code));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return statements.get(code);
	}

	public void createTables() throws SQLException {
		prepare("createTables/tag",
		"createTables/user",
		"createTables/event",
		"createTables/eventEventRelation",
		"createTables/eventTagRelation",
		"createTables/eventUserRelation",
		"createTables/changeLog").execute();
	}

	public EventInfo getEventInfo(int eid) throws SQLException {
		PreparedStatement ps = prepareWithArgs("queries/getEventInfo", eid, Types.INTEGER);
		System.out.println(ps.execute());;
		ResultSet result = ps.getResultSet();

		if (!result.next()) {
			throw new RuntimeException("No event with id " + eid + " was found");
		}
		DateInfo eventDateInfo = new DateInfo(
				result.getString("eventDateType"),
				result.getTimestamp("eventDate1"),
				result.getString("eventDatePrecision"),
				result.getInt("eventDateDiff"),
				result.getString("eventDateDiffType"),
				result.getDate("eventDateDate2")
		);
		TagInfo[] tagInfos = TagInfo.fromArrays((String[]) result.getArray("tagNames").getArray(),
				(int[]) result.getArray("tagColors").getArray());  ARRAYS DONT WORK, grrr, find fix :(
		ChangeInfo[] changeInfos = ChangeInfo.fromArrays((String[]) result.getArray("changeDates").getArray(),
				(String[]) result.getArray("changeAuthorInfos").getArray(), (String[]) result.getArray("changeDescs").getArray());

		EventInfo eventInfo = new EventInfo(
				result.getInt("eid"),
				result.getString("name"),
				result.getString("description"),
				eventDateInfo,
				tagInfos,
				result.getArray("userInfos").getArray(),
				changeInfos
		);

		if (result.next()) {
			throw new RuntimeException("Multiple events were returned for id " + eid + ", when only one is expected");
		}

		return eventInfo;
	}
}
