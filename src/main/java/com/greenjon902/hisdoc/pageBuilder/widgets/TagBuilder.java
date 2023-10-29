package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class TagBuilder implements WidgetBuilder{
	private final int color;
	private final int id;
	private final String name;

	public TagBuilder(String name, int id, int color) {
		this.name = name;
		this.id = id;
		this.color = color;
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		int red = (color & 0xFF0000) >> 16;
		int green = (color & 0x00FF00) >> 8;
		int blue = (color & 0x0000FF);

		stream.write("<div class=\"tag\" style=\"--tag-color-red: ");
		stream.write(String.valueOf(red));
		stream.write("; --tag-color-green: ");
		stream.write(String.valueOf(green));
		stream.write("; --tag-color-blue: ");
		stream.write(String.valueOf(blue));
		stream.write(";\">");
		stream.write("<div class=\"tag-circle\"></div>");
		stream.write("<a class=\"tag-text\" href=\"tag?id=");
		stream.write(String.valueOf(id));
		stream.write("\">");
		stream.writeSafe(name);
		stream.write("</a>");
		stream.write("</div>");
	}
}
