package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public class ContainerWidgetBuilder extends AbstractContainerWidgetBuilder {
	private final String classes;

	public ContainerWidgetBuilder(String classes) {
		this.classes = classes;
	}

	public ContainerWidgetBuilder() {
		this("");
	}

	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<div ");
		if (!classes.isEmpty()) {
			stream.write("class=\"");
			stream.write(classes);
			stream.write("\"");
		}
		stream.write(">");
		renderAllChildren(stream);
		stream.write("</div>");
	}
}
