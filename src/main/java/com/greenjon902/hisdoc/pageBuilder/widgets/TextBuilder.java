package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public class TextBuilder extends AbstractTextBuilder {
	public TextBuilder(String delimiter) {
		super(delimiter);
	}

	public TextBuilder() {
	}

	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<p class=\"text\">");
		renderAllChildren(stream);
		stream.write("</p>");
	}
}
