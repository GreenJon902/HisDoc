package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class TextBuilder extends AbstractTextBuilder {
	private final TextType type;

	public TextBuilder(TextType type) {
		this(type, "");
	}

	public TextBuilder(TextType type, String delimiter) {
		super(delimiter);
		this.type = type;
	}
	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<");
		stream.write(String.valueOf(type.tagType));
		stream.write(" class=\"");
		stream.write(type.cssClass);
		stream.write("\">");
		renderAllChildren(stream, session);
		stream.write("</");
		stream.write(String.valueOf(type.tagType));
		stream.write(">");
		if (type.separator) new SeparatorBuilder(0.2).render(stream, session);
	}
}
