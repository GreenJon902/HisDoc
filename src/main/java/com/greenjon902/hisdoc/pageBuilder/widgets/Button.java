package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class Button implements WidgetBuilder {
	private final String action;
	private final String text;
	private final String tooltip;

	public Button(String text, String action) {
		this(text, action, null);
	}

	public Button(String text, String action, String tooltip) {
		this.text = text;
		this.action = action;
		this.tooltip = tooltip;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<button onclick=\"" + action + "\"");
		if (tooltip != null) {
			stream.write(" title=\"" + tooltip + "\"");
		}
		stream.write(">");
		stream.writeSafe(text);
		stream.write("</button>");
	}
}
