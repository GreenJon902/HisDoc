package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

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
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<div class=\"tag\" style=\"background-color: #");
		stream.write(String.format("%06x", color));
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
