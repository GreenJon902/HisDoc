package com.greenjon902.hisdoc.pageBuilder.widgets;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Creates a timeline filter option selection, this consists of three buttons, "Ex" - Do not allow events with this
 * property, "Ig" - Ig this property when finding events, "In" - In events with the property,
 * "Re" - Events must have this property to be shown.
 * The name of the group is supplied, the IDs of the buttons created are {groupName.hashCode()}-{Ex|Ig|In|Re}.
 */
public class TimelineFilter extends RadioButton {
	private static final List<String> values = List.of("Ex", "Ig", "In", "Re");
	private static final List<String> tooltips = List.of("Events with this will not been shown",
			"This has no effect on whether events will be shown", "Events with this will be shown, given they are not excluded elsewhere", "Events without this will be hidden");

	public TimelineFilter(String groupName, @Nullable String defaultValue, String updateFunc) {
		// If default value is null then that means In
		super(String.valueOf(groupName.hashCode()), (defaultValue == null ? "In" : defaultValue), values, tooltips, updateFunc);
	}
}
