package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.EventInfo;
import com.greenjon902.hisdoc.sql.results.TagInfo;
import com.greenjon902.hisdoc.sql.results.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// TODO: Warn on sets and maps having duplicates

public class Dispatcher {
	Map<String, String> statements = new HashMap<>();
	Connection conn;

	public Dispatcher(Connection conn) {
		this.conn = conn;
	}

	public PreparedStatement prepare(String... codes) throws SQLException {
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

	public PreparedStatement prepareWithArgs(String code, Object... arguments) throws SQLException {
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
		System.out.println("Getting event info for event with eid " + eid + " -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getEventInfo", "eid", eid);
		ps.execute();

		return UnpackHelper.getEventInfo(ps, eid);
	}

	public UserInfo getUserInfo(int uid) throws SQLException {
		System.out.println("Getting user info for user with uid " + uid + " -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getUserInfo", "uid", uid);
		ps.execute();

		return UnpackHelper.getUserInfo(ps, uid);
	}


	public TagInfo getTagInfo(int tid) throws SQLException {
		System.out.println("Getting tag info for tag with tid " + tid + " -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getTagInfo", "tid", tid);
		ps.execute();

		return UnpackHelper.getTagInfo(ps, tid);
	}
}
