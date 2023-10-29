package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class IframeBuilder implements WidgetBuilder {
	private final String src;

	public IframeBuilder(String src) {
		this.src = src;
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write(" <iframe src=\"");
		stream.write(src);
		stream.write("\"></iframe>");
	}
}
