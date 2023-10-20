package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;

import java.sql.SQLException;
import java.util.Map;

public class UserPageRenderer extends PageRenderer {
	private final Dispatcher dispatcher;

	public UserPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public String render(Map<String, String> query, String fragment) throws SQLException {
		if (!query.containsKey("id")) {
			return "No id given :(";
		}

		int userId = Integer.parseInt(query.get("id"));

		return "User for " + userId;
	}
}
