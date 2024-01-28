package com.greenjon902.hisdoc.webDriver;

import com.sun.net.httpserver.Headers;

import java.sql.SQLException;
import java.util.Map;

public abstract class PageRenderer {
	public abstract String render(Map<String, String> query, String fragment, User user) throws SQLException;

	public abstract String contentType();

	public void setHeaders(Headers responseHeaders) {
		responseHeaders.set("Content-Type", contentType());
	}
}
