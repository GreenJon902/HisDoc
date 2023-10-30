package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

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
		PreparedStatement ps = prepareWithArgs("queries/getEventInfo", "eid", eid);
		ps.execute();

		Set<TagLink> tagLinks = TagLink.fromResultSet(ps.getResultSet());

		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find second results");
		}
		Set<UserLink> userLinks = UserLink.fromResultSet(ps.getResultSet());

		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find third results");
		}
		Set<EventLink> eventLinks = Set.copyOf(EventLink.fromResultSet(ps.getResultSet()));

		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find fourth results");
		}
		List<ChangeInfo> changeInfos = ChangeInfo.fromResultSet(ps.getResultSet());

		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find fifth results");
		}
		ResultSet result = ps.getResultSet();
		if (!result.next()) {  // No event found
			if (!tagLinks.isEmpty() || !userLinks.isEmpty() || !changeInfos.isEmpty() || !eventLinks.isEmpty()) {
				throw new RuntimeException("Found no event but found some info relating to the event with id " + eid);
			}
			return null;
		}
		DateInfo eventDateInfo = DateInfo.oneFromResultSet(result);

		Integer postedUid;
		UserLink postedUser = null;
		if ((postedUid = getInteger(result, "postedUid")) != null) {
			postedUser = new UserLink(postedUid, result.getString("postedInfo"));
		}

		EventInfo eventInfo = new EventInfo(
				eid,
				result.getString("name"),
				result.getString("description"),
				result.getTimestamp("postedDate"),
				postedUser,
				eventDateInfo,
				tagLinks,
				userLinks,
				changeInfos,
				eventLinks
		);

		if (result.next()) {
			throw new RuntimeException("Multiple events were returned for id " + eid + ", when only one is expected");
		}
		if (ps.getMoreResults()) {
			throw new RuntimeException("Too many results were returned for id " + eid);
		}

		return eventInfo;
	}

	public UserInfo getUserInfo(int uid) throws SQLException {
		PreparedStatement ps = prepareWithArgs("queries/getUserInfo", "uid", uid);
		ps.execute();

		ResultSet result = ps.getResultSet();
		Map<TagLink, Integer> countedTagLinks = new HashMap<>();
		while (result.next()) {
			TagLink tagLink = new TagLink(result.getInt("tid"), result.getString("name"), result.getInt("color"));
			int count = result.getInt("count");
			countedTagLinks.put(tagLink, count);
		}

		// --------------------------------------------------------------
		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find second results");
		}
		result = ps.getResultSet();
		if (!result.next()) {
			throw new RuntimeException("Could not find post count for user with id " + uid);
		}
		int postCount = result.getInt("count");
		if (result.next()) {
			throw new RuntimeException("Too many post counts for user with id " + uid);
		}

		// --------------------------------------------------------------
		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find third results");
		}
		result = ps.getResultSet();
		if (!result.next()) {
			throw new RuntimeException("Could not find event count for user with id " + uid);
		}
		int eventCount = result.getInt("count");
		if (result.next()) {
			throw new RuntimeException("Too many event counts for user with id " + uid);
		}

		// --------------------------------------------------------------
		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find fourth results");
		}
		List<EventLink> recentEventLinks = EventLink.fromResultSet(ps.getResultSet());

		// --------------------------------------------------------------
		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find fifth results");
		}
		result = ps.getResultSet();
		if (!result.next()) {  // No event found
			if (!countedTagLinks.isEmpty() && !recentEventLinks.isEmpty()) {
				throw new RuntimeException("Found no user but found some info relating to the user with id " + uid);
			}
			return null;
		}

		UserInfo userInfo = new UserInfo(
				uid,
				result.getString("userInfo"),
				countedTagLinks,
				postCount, eventCount, recentEventLinks

		);

		// --------------------------------------------------------------
		if (result.next()) {
			throw new RuntimeException("Multiple users were returned for id " + uid + ", when only one is expected");
		}
		if (ps.getMoreResults()) {
			throw new RuntimeException("Too many results were returned for id " + uid);
		}

		return userInfo;
	}


	public TagInfo getTagInfo(int tid) throws SQLException {
		PreparedStatement ps = prepareWithArgs("queries/getTagInfo", "tid", tid);
		ps.execute();

		List<EventLink> recentEventLinks = EventLink.fromResultSet(ps.getResultSet());

		// --------------------------------------------------------------
		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find second results");
		}
		ResultSet result = ps.getResultSet();
		if (!result.next()) {  // No event found
			if (!recentEventLinks.isEmpty()) {
				throw new RuntimeException("Found no tag but found some info relating to the tid with id " + tid);
			}
			return null;
		}

		TagInfo tagInfo = new TagInfo(
				tid,
				result.getString("name"),
				result.getString("description"),
				result.getInt("color"),
				recentEventLinks
		);

		// --------------------------------------------------------------
		if (result.next()) {
			throw new RuntimeException("Multiple tags were returned for id " + tid + ", when only one is expected");
		}
		if (ps.getMoreResults()) {
			throw new RuntimeException("Too many results were returned for id " + tid);
		}

		return tagInfo;
	}

	public static Integer getInteger(ResultSet result, String name) throws SQLException {
		Integer integer = result.getInt(name);
		if (result.wasNull()) {
			integer = null;
		}
		return integer;
	}
}
