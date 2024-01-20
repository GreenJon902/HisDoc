package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class CheckBoxBuilder implements WidgetBuilder {
	private final WidgetBuilder label;
	private final String name;
	private final boolean defaultActive;

	public CheckBoxBuilder(WidgetBuilder label, String name, boolean defaultActive) {
		this.label = label;
		this.name = name;
		this.defaultActive = defaultActive;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<div class=\"checkbox-container\">");
		stream.write("<input name=\"" + name + "\" id=\"" + name + "\" type=\"checkbox\"");
		if (defaultActive) stream.write(" checked");
		stream.write(">");
		stream.write("<label for=\"" + name + "\">");
		label.render(stream, user);
		stream.write("</label>");
		stream.write("</div>");
	}
}
