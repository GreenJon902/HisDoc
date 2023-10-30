package com.greenjon902.hisdoc.sql.results;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.greenjon902.hisdoc.sql.Dispatcher.getInteger;

public record EventLink(int id, String name, DateInfo dateInfo) {
	public static List<EventLink> fromResultSet(ResultSet result) throws SQLException {
		List<EventLink> eventLinks = new ArrayList<>();
		while (result.next()) {
			DateInfo eventDateInfo = new DateInfo(
					result.getString("eventDateType"),
					result.getTimestamp("eventDate1"),
					result.getString("eventDatePrecision"),
					getInteger(result, "eventDateDiff"),
					result.getString("eventDateDiffType"),
					result.getDate("eventDate2")
			);
			eventLinks.add(new EventLink(result.getInt("eid"), result.getString("name"), eventDateInfo));
		}
		return eventLinks;
	}
}
