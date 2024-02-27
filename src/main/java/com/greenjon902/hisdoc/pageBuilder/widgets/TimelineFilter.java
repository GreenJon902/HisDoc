package com.greenjon902.hisdoc.pageBuilder.widgets;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Creates a timeline filter option selection, this consists of three buttons, "Exclude" - Do not allow events with this
 * property, "Ignore" - Ignore this property when finding events, "Include" - Include events with the property,
 * "Require" - Events must have this property to be shown.
 * The name of the group is supplied, the IDs of the buttons created are {groupName.hashCode()}-{Exclude|Ignore|Include|Require}.
 */
public class TimelineFilter extends RadioButton {
	private static final List<String> values = List.of("Exclude", "Ignore", "Include", "Require");
	private static final List<String> tooltips = List.of("Events with this will not been shown",
			"This has no effect on whether events will be shown", "Events with this will be shown, given they are not excluded elsewhere", "Events without this will be hidden");

	public TimelineFilter(String groupName, @Nullable String defaultValue, String updateFunc) {
		// If default value is null then that means Include
		super(String.valueOf(groupName.hashCode()), (defaultValue == null ? "Include" : defaultValue), values, tooltips, updateFunc);
	}
}
