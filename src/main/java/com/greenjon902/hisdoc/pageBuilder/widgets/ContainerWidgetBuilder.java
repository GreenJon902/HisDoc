package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class ContainerWidgetBuilder extends AbstractContainerWidgetBuilder {
	private final String classes;
	private final String style;

	public ContainerWidgetBuilder(String classes) {
		this(classes, "");
	}

	public ContainerWidgetBuilder(String classes, String style) {
		this.classes = classes;
		this.style = style;
	}

	public ContainerWidgetBuilder() {
		this("");
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<div");
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
		renderAllChildren(stream, session);
		stream.write("</div>");
	}
}
