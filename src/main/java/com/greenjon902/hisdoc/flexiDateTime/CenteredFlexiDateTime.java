package com.greenjon902.hisdoc.flexiDateTime;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * A datetime which may be a bit off on either side.
 * E.g. With center 5, units days, and diff 3. This means the date is anywhere between 2 and 8 days after the unix epoch
 * ('1970-01-01 00:00:00' UTC).
 * Effectively <i>center</i> <i>units</i>s +- <i>diff</i> <i>units</i>s
 */
public class CenteredFlexiDateTime extends FlexiDateTime {
	public final long center;
	public final @NotNull Units units;
	public final long diff;

	/**
	 * Creates a new centered flexi date time.
	 * @param center The center, in whatever units that is stored in value
	 * @param units What each number is worth, this applies to both center and diff
	 * @param diff The difference, in whatever units that is stored in value
	 */
	public CenteredFlexiDateTime(long center, @NotNull Units units, long diff) {
		this.center = center;
		this.units = units;
		this.diff = diff;
	}

	@Override
	public String toString() {
		return "CenteredFlexiDateTime{" +
				"center=" + center +
				", units=" + units +
				", diff=" + diff +
				'}';
	}

	@Override
	public long earliestUnix() {
		return (center - diff) * units.value;
	}

	@Override
	public long latestUnix() {
		return (center + diff) * units.value;
	}

	@Override
	public String formatString() {
		String pattern =
				switch (units) {
					case SECOND -> "yyyy-MM-dd HH:mm:ss";
					case MINUTE -> "yyyy-MM-dd HH:mm";
					case HOUR -> "yyyy-MM-dd HH:??";
					case DAY -> "yyyy-MM-dd";
				};

		SimpleDateFormat format = new SimpleDateFormat(pattern);
		format.setTimeZone(TimeZone.getTimeZone("Europe/London"));

		String centerString = format.format(new Date(center * units.value * 1000));  // Takes in ms
		String diffString = "";
		if (diff != 0) {
			diffString = " ±" + diff + units.sqlId.toUpperCase(Locale.ROOT);
		}
		return centerString + diffString;
	}

	/**
	 * The units of a number in here, which means the worth. So if a number was 5 and the units were minutes, then
	 * that would mean 5 minutes.
	 */
	public enum Units {
		DAY("d", 24 * 60 * 60, ChronoUnit.DAYS),
		HOUR("h", 60 * 60, ChronoUnit.HOURS),
		MINUTE("m", 60, ChronoUnit.MINUTES),
		SECOND("s", 1, ChronoUnit.SECONDS);

		private static final Map<String, Units> decodeMap;
		static {
			HashMap<String, Units> mutDecodeMap = new HashMap<>();
			for (Units precision : values()) {
				mutDecodeMap.put(precision.sqlId.toLowerCase(), precision);
			}
			decodeMap = Collections.unmodifiableMap(mutDecodeMap);
		}

		public static Units decode(String string) {
			if (string == null) return null;
			return decodeMap.get(string.toLowerCase());
		}

		public final String sqlId;
		public final long value; // In seconds
		public final TemporalUnit temporalUnit;

		Units(String sqlId, long value, TemporalUnit temporalUnit) {
			this.sqlId = sqlId;
			this.value = value;
			this.temporalUnit = temporalUnit;
		}
	}
}
