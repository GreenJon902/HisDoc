package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class ColumnLayoutBuilder extends AbstractContainerWidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<div class=\"column-container\">");
		renderAllChildren(stream, session);
		stream.write("</div>");
	}
}
