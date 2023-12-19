package com.greenjon902.hisdoc.flexiDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * A datetime which may be anywhere within a certain range.
 * E.g. With date1 being 1000/01/01 and date2 being 1000/01/05,
 * the date could be anywhere between 1000/01/01 and 1000/01/05.
 *
 * Note: this only supports dates
 */
public class RangedFlexiDate extends FlexiDateTime {
	private static final long value = 24 * 60 * 60; // The value of one day in seconds
	private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  // TODO: Timezone this?

	public final long start;
	public final long end;

	/**
	 * Creates a new RangedFlexiDate.
	 * @param date1 The first of the range
	 * @param date2 The second of the range
	 */
	public RangedFlexiDate(long date1, long date2) {
		if (date1 > date2) { // Ensure dates are the correct way round
			long temp = date1;
			date1 = date2;
			date2 = temp;
		}

		start = date1;
		end = date2;
	}

	@Override
	public long earliestUnix() {
		return start * value;
	}

	@Override
	public String toString() {
		return "RangedFlexiDate{" +
				"start=" + start +
				", end=" + end +
				'}';
	}

	@Override
	public long latestUnix() {
		return end * value;
	}

	@Override
	public String formatString() {
		String start = formatter.format(new Date(earliestUnix() * 1000));  // Takes in ms
		String end = formatter.format(new Date(latestUnix() * 1000));

		return "Somewhere between " + start + " and " + end;
	}
}
