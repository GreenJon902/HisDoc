package com.greenjon902.hisdoc.sql.results;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public record TagLink(int id, String name, int color) {
	public static Set<TagLink> fromResultSet(ResultSet result) throws SQLException {
		Set<TagLink> tagLinks = new HashSet<>();
		while (result.next()) {
			tagLinks.add(new TagLink(result.getInt("tid"), result.getString("name"), result.getInt("color")));
		}
		return tagLinks;
	}
}
