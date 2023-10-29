package com.greenjon902.hisdoc.webDriver;

import java.sql.SQLException;
import java.util.Map;

public abstract class PageRenderer {
	public abstract String render(Map<String, String> query, String fragment, Session session) throws SQLException;
}
