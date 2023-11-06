package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class Form extends AbstractContainerWidgetBuilder {
	private final String id;

	public Form(String id) {
		this.id = id;
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<form id=\"" + id + "\">");
		renderAllChildren(stream, session);
		stream.write("</form>");
	}
}
