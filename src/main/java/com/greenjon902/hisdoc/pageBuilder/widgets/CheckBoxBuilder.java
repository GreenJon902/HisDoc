package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class CheckBoxBuilder implements WidgetBuilder {
	private final WidgetBuilder label;
	private final String name;

	public CheckBoxBuilder(WidgetBuilder label, String name) {
		this.label = label;
		this.name = name;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<div class=\"checkbox-container\">");
		stream.write("<input name=\"" + name + "\" id=\"" + name + "\" type=\"checkbox\">");
		stream.write("<label for=\"" + name + "\">");
		label.render(stream, user);
		stream.write("</label>");
		stream.write("</div>");
	}
}
