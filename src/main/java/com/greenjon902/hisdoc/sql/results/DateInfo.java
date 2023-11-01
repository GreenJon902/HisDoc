package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.Timestamp;


// TODO: Convert some types to enums
public record DateInfo(@NotNull String type, @NotNull Timestamp date1, @Nullable String precision, @Nullable Integer diff, @Nullable String diffType, @Nullable Date date2) {
	public DateInfo {
		if (type.equals("c")) {
			assert precision != null;
			assert diff != null;
			assert diffType != null;
			assert date2 == null;
		} else if (type.equals("b")) {
			assert precision == null;
			assert diff == null;
			assert diffType == null;
			assert date2 != null;
		} else {
			throw new IllegalArgumentException("Invalid value for type: " + type);
		}
	}

	public static DateInfo centered(Timestamp center, String precision, int diff, String diffType) {
		return new DateInfo("c", center, precision, diff, diffType, null);
	}

	public static DateInfo between(Timestamp date1, Date date2) {
		return new DateInfo("b", date1, null, null, null, date2);
	}


}
