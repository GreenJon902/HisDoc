package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class TagBuilder implements WidgetBuilder{
	private final int color;
	protected final int id;
	private final String name;
	private final boolean link;
	private final String description;

	public TagBuilder(String name, int id, int color, String description) {
		this(name, id, color, true, description);
	}

	/**
	 * Creates a new tag builder.
	 * @param name The name of the tag
	 * @param id The id of the tag, used for linking
	 * @param color The color of the tag
	 * @param link Should it direct you to the tag page when it is pressed
	 * @param description The description of the tag, used as a tooltip
	 */
	public TagBuilder(String name, int id, int color, boolean link, String description) {
		this.name = name;
		this.id = id;
		this.color = color;
		this.link = link;
		this.description = description;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		int red = (color & 0xFF0000) >> 16;
		int green = (color & 0x00FF00) >> 8;
		int blue = (color & 0x0000FF);

		stream.write("<div title=\"");
		stream.write(description);
		stream.write("\"class=\"tag\" style=\"--tag-color-red: ");
		stream.write(String.valueOf(red));
		stream.write("; --tag-color-green: ");
		stream.write(String.valueOf(green));
		stream.write("; --tag-color-blue: ");
		stream.write(String.valueOf(blue));
		stream.write(";\">");
		renderCircle(stream);
		stream.write("<a class=\"tag-text\"");
		if (link) {
			stream.write(" href=\"tag?id=");
			stream.write(String.valueOf(id));
			stream.write("\"");
		}
		stream.write(">");
		stream.writeSafe(name);
		stream.write("</a>");
		stream.write("</div>");
	}

	protected void renderCircle(HtmlOutputStream stream) throws IOException {
		stream.write("<div class=\"tag-circle\"></div>");
	}
}
