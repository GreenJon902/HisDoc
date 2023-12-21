package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class TextBuilder extends AbstractTextBuilder {
	private final TextType type;
	private final @Nullable String id;

	public TextBuilder(TextType type) {
		this(type, "", null);
	}

	public TextBuilder(TextType type, String delimiter, @Nullable String id) {
		super(delimiter);
		this.type = type;
		this.id = id;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<");
		stream.write(String.valueOf(type.tagType));
		if (id != null) {
			stream.write(" id=\"");
			stream.writeSafe(id);
			stream.write("\"");
		}
		stream.write(" class=\"");
		stream.write(type.cssClass);
		stream.write("\">");
		if (type.prefix != null) {
			stream.write(type.prefix);
		}
		renderAllChildren(stream, user);
		stream.write("</");
		stream.write(String.valueOf(type.tagType));
		stream.write(">");
		if (type.separator) new SeparatorBuilder(0.2).render(stream, user);
	}
}
