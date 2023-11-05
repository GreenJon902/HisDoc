package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

/**
 * Creates a timeline filter option selection, this consists of three buttons, "Exclude" - Do not allow events with this
 * property, "Ignore" - Ignore this property when finding events, "Include" - Include events with the property.
 * The name of the group is supplied, the IDs of the buttons created are {groupName}-{Exclude|Ignore|Include}.
 */
public class TimelineFilter implements WidgetBuilder {
	private final String groupName;

	public TimelineFilter(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<div class=\"timeline-filter-holder\">");
		renderButton(stream, "Exclude", false);
		renderButton(stream, "Ignore", true);
		renderButton(stream, "Include", false);
		stream.write("</div>");
	}

	private void renderButton(HtmlOutputStream stream, String name, boolean defaultActive) throws IOException {
		// defaultActive is whether it is the default thing to be selected
		stream.write("<input type=\"radio\" class=\"timeline-filter\" " +
				"id=\"" + groupName + "-" + name + "\" " +
				"value=\"" + groupName + "-" + name + "\" " +
				"name=\"" + groupName + "\"" + (defaultActive ? " checked" : "") + ">" +
				"<label class=\"timeline-filter\" for=\"" + groupName + "-" + name + "\">" + name + "</label>");
	}
}
