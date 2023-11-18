package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class ImageBuilder implements WidgetBuilder {
	private final String src;

	public ImageBuilder(String src) {
		this.src = src;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write(" <img src=\"");
		stream.write(src);
		stream.write("\">");
	}
}
