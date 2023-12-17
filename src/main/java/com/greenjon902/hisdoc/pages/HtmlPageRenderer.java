package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.webDriver.PageRenderer;

public abstract class HtmlPageRenderer extends PageRenderer {
	@Override
	public String contentType() {
		return "text/html";
	}
}
