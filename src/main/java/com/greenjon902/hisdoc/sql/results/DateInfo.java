package com.greenjon902.hisdoc.sql.results;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.greenjon902.hisdoc.sql.Dispatcher.getInteger;

// TODO: Convert some types to enums
public record DateInfo(String type, Timestamp date1, String precision, Integer diff, String diffType, Date date2) {

	public static DateInfo centered(Timestamp center, String precision, int diff, String diffType) {
		return new DateInfo("c", center, precision, diff, diffType, null);
	}

	public static DateInfo between(Timestamp date1, Date date2) {
		return new DateInfo("b", date1, null, null, null, date2);
	}

	public static DateInfo oneFromResultSet(ResultSet result) throws SQLException {
		DateInfo eventDateInfo = new DateInfo(
				result.getString("eventDateType"),
				result.getTimestamp("eventDate1"),
				result.getString("eventDatePrecision"),
				getInteger(result, "eventDateDiff"),
				result.getString("eventDateDiffType"),
				result.getDate("eventDate2")
		);
		return eventDateInfo;
	}
}
