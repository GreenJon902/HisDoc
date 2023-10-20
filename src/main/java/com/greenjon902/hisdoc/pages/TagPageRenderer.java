package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.PageRenderer;

import java.sql.SQLException;
import java.util.Map;

public class TagPageRenderer extends PageRenderer {
	private final Dispatcher dispatcher;

	public TagPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public String render(Map<String, String> query, String fragment) throws SQLException {
		if (!query.containsKey("id")) {
			return "No id given :(";
		}

		int tagId = Integer.parseInt(query.get("id"));

		return "Tag for " + tagId;
	}
}
