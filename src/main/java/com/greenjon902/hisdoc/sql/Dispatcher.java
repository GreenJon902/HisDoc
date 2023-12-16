package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.pages.AddEventSubmitPageRenderer;
import com.greenjon902.hisdoc.sql.results.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
			if (!(arguments[i * 2 + 1] instanceof Integer)) {
				throw new IllegalArgumentException("Can only pass integer arguments, anything else should use ? args");
			}
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
		"createTables/person",
		"createTables/event",
		"createTables/eventEventRelation",
		"createTables/eventTagRelation",
		"createTables/eventPersonRelation",
		"createTables/changeLog").execute();
	}

	public EventInfo getEventInfo(int eid) throws SQLException {
		System.out.println("Getting event info for event with id " + eid + " -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getEventInfo", "eid", eid);
		ps.execute();

		return UnpackHelper.getEventInfo(ps);
	}

	public PersonInfo getPersonInfo(int pid) throws SQLException {
		System.out.println("Getting person info for person with id " + pid + " -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getPersonInfo", "pid", pid);
		ps.execute();

		return UnpackHelper.getPersonInfo(ps);
	}


	public TagInfo getTagInfo(int tid) throws SQLException {
		System.out.println("Getting tag info for tag with id " + tid + " -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getTagInfo", "tid", tid);
		ps.execute();

		return UnpackHelper.getTagInfo(ps);
	}

	public TimelineInfo getTimelineInfo() throws SQLException {
		System.out.println("Getting timeline info -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getTimelineInfo");
		ps.execute();

		return UnpackHelper.getTimelineInfo(ps);
	}

	public Set<TagLink> getAllTagLinks() throws SQLException {
		System.out.println("Getting all tag links -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getAllTagLinks");
		ps.execute();

		return UnpackHelper.getSet(ps.getResultSet(), UnpackHelper::getTagLink);
	}

	public Set<PersonLink> getAllPersonLinks() throws SQLException {
		System.out.println("Getting all person links -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getAllPersonLinks");
		ps.execute();

		return UnpackHelper.getSet(ps.getResultSet(), UnpackHelper::getPersonLink);
	}

	public int addEvent(AddEventSubmitPageRenderer.SubmittedEvent submittedEvent) throws SQLException {
		// Add the actual event
		System.out.println("Adding event link -----------------");
		System.out.println(submittedEvent);
		PreparedStatement ps = prepareWithArgs("upload/addEvent");
		ps.setString(1, submittedEvent.name());
		ps.setString(11, submittedEvent.name());  // For getting the event id
		ps.setString(2, submittedEvent.description());
		ps.setString(3, submittedEvent.details());
		ps.setString(4, submittedEvent.dateInfo().type().sqlId);
		ps.setTimestamp(5, submittedEvent.dateInfo().date1());
		if (submittedEvent.dateInfo().precision() == null) {
			ps.setNull(6, Types.VARCHAR);
		} else {
			ps.setString(6, submittedEvent.dateInfo().precision().sqlId);
		}
		if (submittedEvent.dateInfo().diff() == null) {
			ps.setNull(7, Types.INTEGER);
		} else {
			ps.setInt(7, submittedEvent.dateInfo().diff());
		}
		if (submittedEvent.dateInfo().diffType() == null) {
			ps.setNull(8, Types.VARCHAR);
		} else {
			ps.setString(8, submittedEvent.dateInfo().diffType().sqlId);
		}
		ps.setDate(9, submittedEvent.dateInfo().date2());
		ps.setInt(10, submittedEvent.postedBy());
		ps.execute();

		// Get eid
		ps.getMoreResults();
		ResultSet resultSet =  ps.getResultSet();
		resultSet.next();
		int eid = resultSet.getInt("eid");
		if (ps.getMoreResults()) {
			throw new RuntimeException("Too many event with the same name, name=\"" + submittedEvent.name() + "\"");
		}

		// Add relations
		System.out.println("Adding tag relations --");
		PreparedStatement tagPs = prepareWithArgs("upload/addTagRelation");
		for (int tagId : submittedEvent.tagIds()) {
			tagPs.setInt(1, eid);
			tagPs.setInt(2, tagId);
			tagPs.execute();
		}

		System.out.println("Adding person relations --");
		PreparedStatement personPs = prepareWithArgs("upload/addPersonRelation");
		for (int personId : submittedEvent.personIds()) {
			personPs.setInt(1, eid);
			personPs.setInt(2, personId);
			personPs.execute();
		}

		System.out.println("Adding event relations --");
		PreparedStatement eventPs = prepareWithArgs("upload/addEventRelation");
		for (int eventId : submittedEvent.relatedEventIds()) {
			eventPs.setInt(1, eid);
			eventPs.setInt(2, eventId);
			eventPs.execute();
		}

		return eid;
	}

	/**
	 * Gets the person id from a minecraft UUID, returns null if none found.
	 */
	public Integer getPersonIdFromMcUUID(UUID uniqueId) throws SQLException {
		System.out.println("Getting player for mcUUID " + uniqueId + " -----------------");
		PreparedStatement ps = prepareWithArgs("queries/getAllPersonLinks",
				"personType", "MC");
		ps.setString(1, uniqueId.toString());
		ps.execute();

		ResultSet result = ps.getResultSet();
		UnpackHelper.next(result, "personId", "the");
		Integer pid = UnpackHelper.getInteger(result, "pid");
		if (result.next()) {
			throw new RuntimeException("Got multiple persons with uuid " + uniqueId);
		}
		return pid;
	}
}
