package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class Button implements WidgetBuilder {
	private final String action;
	private final String text;

	public Button(String text, String action) {
		this.text = text;
		this.action = action;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<button onclick=\"" + action + "\">");
		stream.writeSafe(text);
		stream.write("</button>");
	}
}
