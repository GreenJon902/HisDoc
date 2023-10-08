package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.*;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

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
		StringBuilder sql = new StringBuilder();
		sql.append(checkStatement("statement/start"));
		String statement = checkStatement(code);
		for (int i=0; i < arguments.length / 2; i++) {
			statement = statement.replace("{" + (String) arguments[i * 2] + "}", arguments[i * 2 + 1].toString());
		}
		sql.append(statement);
		sql.append(checkStatement("statement/end"));

		return conn.prepareStatement(sql.toString());
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
		PreparedStatement ps = prepareWithArgs("queries/getEventInfo", "eid", eid);
		ps.execute();

		ResultSet result = ps.getResultSet();
		Set<TagInfo> tagInfos = new HashSet<>();
		while (result.next()) {
			tagInfos.add(new TagInfo(result.getInt("tid"), result.getString("name"), result.getInt("color")));
		}

		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find second results");
		}
		result = ps.getResultSet();
		Set<UserInfo> userInfos = new HashSet<>();
		while (result.next()) {
			userInfos.add(new UserInfo(result.getInt("uid"), result.getString("userInfo")));
		}

		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find third results");
		}
		result = ps.getResultSet();
		Set<ChangeInfo> changeInfos = new HashSet<>();
		while (result.next()) {
			changeInfos.add(new ChangeInfo(result.getTimestamp("date"),
					new UserInfo(result.getInt("authorUid"), result.getString("authorInfo")),
					result.getString("description")));
		}

		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find fourth results");
		}
		result = ps.getResultSet();
		if (!result.next()) {  // No event found
			if (!tagInfos.isEmpty() || !userInfos.isEmpty() || !changeInfos.isEmpty()) {
				throw new RuntimeException("Found no event but found some info relating to the event with id " + eid);
			}
			return null;
		}
		DateInfo eventDateInfo = new DateInfo(
				result.getString("eventDateType"),
				result.getTimestamp("eventDate1"),
				result.getString("eventDatePrecision"),
				getInteger(result, "eventDateDiff"),
				result.getString("eventDateDiffType"),
				result.getDate("eventDate2")
		);

		Integer postedUid;
		UserInfo postedUser = null;
		if ((postedUid = getInteger(result, "postedUid")) != null) {
			postedUser = new UserInfo(postedUid, result.getString("postedInfo"));
		}

		EventInfo eventInfo = new EventInfo(
				eid,
				result.getString("name"),
				result.getString("description"),
				result.getTimestamp("postedDate"),
				postedUser,
				eventDateInfo,
				tagInfos,
				userInfos,
				changeInfos
		);

		if (result.next()) {
			throw new RuntimeException("Multiple events were returned for id " + eid + ", when only one is expected");
		}
		if (ps.getMoreResults()) {
			throw new RuntimeException("Too many results were returned for id " + eid);
		}

		return eventInfo;
	}

	private Integer getInteger(ResultSet result, String name) throws SQLException {
		Integer integer = result.getInt(name);
		if (result.wasNull()) {
			integer = null;
		}
		return integer;
	}
}
