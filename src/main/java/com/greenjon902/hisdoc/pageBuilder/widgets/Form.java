package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class Form extends AbstractContainerWidgetBuilder {
	private final String id;

	public Form(String id) {
		this.id = id;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<form id=\"" + id + "\">");
		renderAllChildren(stream, user);
		stream.write("</form>");
	}
}
