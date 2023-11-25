package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class SelectableTagBuilder extends TagBuilder {
	public SelectableTagBuilder(String name, int id, int color) {
		super(name, id, color, false);
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<input name=\"tag" + id + "\" id=\"tag" + id + "\" " +
				"onchange=\"document.getElementById('check-for-tag" + id + "').style.display = (this.checked) ? '' : 'none'\" " +
				"type=\"checkbox\" class=\"tag-selector\">");
		stream.write("<label for=\"tag" + id + "\" class=\"tag-selector\">");
		super.render(stream, user);
		stream.write("</label>");
	}

	@Override
	protected void renderCircle(HtmlOutputStream stream) throws IOException {
		stream.write("<div class=\"tag-selector-circle fa-solid fa-circle\"></div>");
		stream.write("<div id=\"check-for-tag" + id + "\" style=\"display: none;\" class=\"tag-selector-check fa-regular fa-circle-check\"></div>");
	}
}
