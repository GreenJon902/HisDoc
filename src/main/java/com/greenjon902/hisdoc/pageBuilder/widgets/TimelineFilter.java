package com.greenjon902.hisdoc.pageBuilder.widgets;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Creates a timeline filter option selection, this consists of three buttons, "Exclude" - Do not allow events with this
 * property, "Ignore" - Ignore this property when finding events, "Include" - Include events with the property.
 * The name of the group is supplied, the IDs of the buttons created are {groupName}-{Exclude|Ignore|Include}.
 */
public class TimelineFilter extends RadioButton {
	private static final List<String> values = List.of("Exclude", "Ignore", "Include");

	public TimelineFilter(String groupName, @Nullable String defaultValue, String updateFunc) {
		super(groupName, (defaultValue == null ? "Include" : defaultValue), values, updateFunc);
	}
}
