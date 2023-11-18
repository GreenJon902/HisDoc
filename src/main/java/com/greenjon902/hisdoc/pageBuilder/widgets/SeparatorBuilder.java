package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class SeparatorBuilder implements WidgetBuilder {
	private final double thickness;  // -1 for default

	public SeparatorBuilder(double thickness) {
		this.thickness = thickness;
	}

	public SeparatorBuilder() {
		this(-1);
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<hr class=\"");
		if (thickness == -1) {
			stream.write("separator\"");
		} else {
			stream.write("thick-separator\" style=\"height: ");
			stream.write(String.valueOf(thickness));
			stream.write("em;\"");
		}
		stream.write(">");
	}
}
