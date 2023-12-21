package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class ContainerWidgetBuilder extends AbstractContainerWidgetBuilder {
	private final String classes;
	private final String style;
	private final String id;

	public ContainerWidgetBuilder(String classes) {
		this(classes, "");
	}

	public ContainerWidgetBuilder(String classes, String style) {
		this("", classes, style);
	}

	public ContainerWidgetBuilder(String id, String classes, String style) {
		this.id = id;
		this.classes = classes;
		this.style = style;
	}

	public ContainerWidgetBuilder() {
		this("");
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<div");
		if (!id.isEmpty()) {
			stream.write(" id=\"");
			stream.writeSafe(id);
			stream.write("\"");
		}
		if (!classes.isEmpty()) {
			stream.write(" class=\"");
			stream.write(classes);
			stream.write("\"");
		}
		if (!style.isEmpty()) {
			stream.write(" style=\"");
			stream.write(style);
			stream.write("\"");
		}
		stream.write(">");
		renderAllChildren(stream, user);
		stream.write("</div>");
	}
}
