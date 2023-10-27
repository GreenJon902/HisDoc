package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public class ColumnLayoutBuilder extends AbstractContainerWidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<div class=\"column-container\">");
		renderAllChildren(stream);
		stream.write("</div>");
	}
}
