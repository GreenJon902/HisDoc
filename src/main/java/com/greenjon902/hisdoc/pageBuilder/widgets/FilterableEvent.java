package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.scripts.TimelineSearchFilterScript;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

/**
 * An event on the timeline that can be filtered, see the {@link TimelineFilter} and the {@link TimelineSearchFilterScript}
 */
public class FilterableEvent extends AbstractContainerWidgetBuilder {
	public final String eventName;
	private final boolean defaultDisabled;

	public FilterableEvent(String eventName, boolean defaultDisabled) {
		this.eventName = eventName;
		this.defaultDisabled = defaultDisabled;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<div id=\"");
		stream.writeSafe(eventName);
		stream.write("\"");
		if (defaultDisabled) {
			stream.write(" disabled");
		}
		stream.write(">");
		renderAllChildren(stream, user);
		stream.write("</div>");
	}
}
