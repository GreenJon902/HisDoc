package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public class BreakBuilder implements WidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<br>");
	}
}
