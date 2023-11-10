package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

/**
 * Creates a timeline filter option selection, this consists of three buttons, "Exclude" - Do not allow events with this
 * property, "Ignore" - Ignore this property when finding events, "Include" - Include events with the property.
 * The name of the group is supplied, the IDs of the buttons created are {groupName}-{Exclude|Ignore|Include}.
 *
 * These are rigged up to the run the function `refilterEvents()` when they change.
 */
public class TimelineFilter implements WidgetBuilder {
	private final String groupName;
	private final String defaultValue;  // TODO: Enum this?

	public TimelineFilter(String groupName, String defaultValue) {
		this.groupName = groupName;
		this.defaultValue = (defaultValue == null ? "" : defaultValue);
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<div class=\"timeline-filter-holder\">");
		renderButton(stream, "Exclude", defaultValue.equals("Exclude"));
		renderButton(stream, "Ignore", defaultValue.equals("Ignore"));
		renderButton(stream, "Include", !(defaultValue.equals("Exclude") || defaultValue.equals("Ignore")));
		stream.write("</div>");
	}

	private void renderButton(HtmlOutputStream stream, String name, boolean defaultActive) throws IOException {
		// defaultActive is whether it is the default thing to be selected
		stream.write("<input type=\"radio\" class=\"timeline-filter\" " +
				"id=\"" + id(name) + "\"" +
				"value=\"" + name + "\" " +
				"name=\"" + groupName + "\"" + (defaultActive ? " checked" : "") +
				" onchange=\"filterChanged()\">" +
				"<label class=\"timeline-filter\" for=\"" + id(name) + "\">" + name + "</label>");
	}

	public String id(String name) {
		return id() + "-" + name;
	}

	public String id() {
		return groupName;
	}
}
