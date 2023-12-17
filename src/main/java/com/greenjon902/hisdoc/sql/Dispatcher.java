package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.pages.AddEventSubmitPageRenderer;
import com.greenjon902.hisdoc.sql.results.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class Dispatcher {
	private final Map<String, String> statements = new HashMap<>();
	private final Connection conn;
	private final Logger logger;

	public Dispatcher(Connection conn, Logger logger) {
		this.conn = conn;
		this.logger = logger;
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

		logger.finest(() -> "Prepared \"" + code + "\" with arguments " + Arrays.toString(arguments) + ":\n" + sql);

		return conn.prepareStatement(sql.toString());
	}

	private String checkStatement(String code) {
		if (!statements.containsKey(code)) {
			try {
				logger.fine("Loading sql script \"" + code + "\"");
				InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/sql/statements/" + code + ".sql");
				statements.put(code, new String(fileInputStream.readAllBytes()).replace("{prefix}", "hs_"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return statements.get(code);
	}

	public void createTables() throws SQLException {
		logger.finer("Creating tables");
		prepare("createTables/tag",
		"createTables/person",
		"createTables/event",
		"createTables/eventEventRelation",
		"createTables/eventTagRelation",
		"createTables/eventPersonRelation",
		"createTables/changeLog").execute();
	}

	public EventInfo getEventInfo(int eid) throws SQLException {
		logger.finer("Getting event info for event with id " + eid);
		PreparedStatement ps = prepareWithArgs("queries/getEventInfo", "eid", eid);
		ps.execute();

		EventInfo eventInfo = UnpackHelper.getEventInfo(ps);
		logger.finest(() -> "Got " + eventInfo);
		return eventInfo;
	}

	public PersonInfo getPersonInfo(int pid) throws SQLException {
		logger.finer("Getting person info for person with id " + pid);
		PreparedStatement ps = prepareWithArgs("queries/getPersonInfo", "pid", pid);
		ps.execute();

		PersonInfo personInfo = UnpackHelper.getPersonInfo(ps);
		logger.finest(() -> "Got " + personInfo);
		return personInfo;
	}


	public TagInfo getTagInfo(int tid) throws SQLException {
		logger.finer("Getting tag info for tag with id " + tid);
		PreparedStatement ps = prepareWithArgs("queries/getTagInfo", "tid", tid);
		ps.execute();

		TagInfo tagInfo = UnpackHelper.getTagInfo(ps);
		logger.finest(() -> "Got " + tagInfo);
		return tagInfo;
	}

	public TimelineInfo getTimelineInfo() throws SQLException {
		logger.finer("Getting timeline info");
		PreparedStatement ps = prepareWithArgs("queries/getTimelineInfo");
		ps.execute();

		TimelineInfo timelineInfo = UnpackHelper.getTimelineInfo(ps);
		logger.finest(() -> "Got " + timelineInfo);
		return timelineInfo;
	}

	public Set<TagLink> getAllTagLinks() throws SQLException {
		logger.finer("Getting all tag links");
		PreparedStatement ps = prepareWithArgs("queries/getAllTagLinks");
		ps.execute();

		Set<TagLink> tagLinks = UnpackHelper.getSet(ps.getResultSet(), UnpackHelper::getTagLink);
		logger.finest(() -> "Got " + tagLinks);
		return tagLinks;
	}

	public Set<PersonLink> getAllPersonLinks() throws SQLException {
		logger.finer("Getting all person links");
		PreparedStatement ps = prepareWithArgs("queries/getAllPersonLinks");
		ps.execute();

		Set<PersonLink> personLinks = UnpackHelper.getSet(ps.getResultSet(), UnpackHelper::getPersonLink);
		logger.finest(() -> "Got " + personLinks);
		return personLinks;
	}

	public int addEvent(AddEventSubmitPageRenderer.SubmittedEvent submittedEvent) throws SQLException {
		// Add the actual event
		logger.finer(() -> "Adding event link for " + submittedEvent);
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
		logger.finer("Adding tag relations");
		PreparedStatement tagPs = prepareWithArgs("upload/addTagRelation");
		for (int tagId : submittedEvent.tagIds()) {
			tagPs.setInt(1, eid);
			tagPs.setInt(2, tagId);
			tagPs.execute();
		}

		logger.finer("Adding person relations");
		PreparedStatement personPs = prepareWithArgs("upload/addPersonRelation");
		for (int personId : submittedEvent.personIds()) {
			personPs.setInt(1, eid);
			personPs.setInt(2, personId);
			personPs.execute();
		}

		logger.finer("Adding event relations");
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
		logger.finer("Getting player for mcUUID " + uniqueId);
		PreparedStatement ps = prepareWithArgs("queries/getPerson");
		ps.setString(1, "MC");
		ps.setString(2, uniqueId.toString());
		ps.execute();

		ResultSet result = ps.getResultSet();
		UnpackHelper.next(result, "personId", "the");
		Integer pid = UnpackHelper.getInteger(result, "pid");
		if (result.next()) {
			throw new RuntimeException("Got multiple persons with uuid " + uniqueId);
		}
		logger.finest("Got " + pid);
		return pid;
	}
}
