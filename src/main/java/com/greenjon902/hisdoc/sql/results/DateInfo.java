package com.greenjon902.hisdoc.sql.results;

import java.sql.Date;
import java.sql.Timestamp;

public record DateInfo(String type, Timestamp date1, String precision, int diff, String diffType, Date date2) {

	public static DateInfo centered(Timestamp center, String precision, int diff, String diffType) {
		return new DateInfo("c", center, precision, diff, diffType, null);
	}
}
