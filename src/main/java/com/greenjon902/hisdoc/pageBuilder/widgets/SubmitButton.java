package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class SubmitButton implements WidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<input type=\"submit\">");
	}
}
