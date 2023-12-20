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
	 * Gets the earliest time this date could be in seconds after the unix epoch ('1970-01-01 00:00:00' UTC)
	 */
	public abstract long earliestUnix();

	/**
	 * Gets the latest time this date could be in seconds after the unix epoch ('1970-01-01 00:00:00' UTC)
	 */
	public abstract long latestUnix();

	/**
	 * Formats this flexible date time in a human readable way.
	 */
	public abstract String formatString();
}
