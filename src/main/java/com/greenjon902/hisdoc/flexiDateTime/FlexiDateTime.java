package com.greenjon902.hisdoc.flexiDateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class which stores a flexible date time, this means it gives a range of dates that an event could have occurred
 * within.
 */
public abstract class FlexiDateTime {

	/**
	 * Gets the earliest time this date could be in seconds after the unix epoch ('1970-01-01 00:00:00' UTC+(offset)).
	 * That means this date may not be in UTC.
	 */
	public abstract long earliestUnix();

	/**
	 * Gets the latest time this date could be in seconds after the unix epoch ('1970-01-01 00:00:00' UTC+(offset)).
	 * That means this date may not be in UTC.
	 */
	public abstract long latestUnix();

	/**
	 * Gets the offset in minutes from UTC, this is needed as if a date has only a date component, different time zones
	 * that date will start at different times.
	 */
	public abstract int offset();

	/**
	 * See {@link #offset()}.
	 */
	public String formatOffset() {
		String timezoneInfo = " UTC";
		int offset = offset();

		if (offset != 0) {
			int absOffset = Math.abs(offset);
			String sign = (offset / absOffset >= 0 ? "+" : "-");

			String hours = String.valueOf(absOffset / 60);  // Keep sign on this one
			if (hours.length() == 1) hours = "0" + hours;
			String mins = String.valueOf(absOffset % 60);
			if (mins.length() == 1) mins = "0" + mins;

			timezoneInfo += sign + hours + ":" + mins;
		}

		return timezoneInfo;
	}

	/**
	 * Formats this flexible date time in a human-readable way. This may include timezones
	 */
	public abstract String formatString();
}
