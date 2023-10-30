package com.greenjon902.hisdoc.sql.results;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public record UserLink(int id, String userInfo) {  // TODO: Decode userInfo
	public static Set<UserLink> fromResultSet(ResultSet result) throws SQLException {
		Set<UserLink> userLinks = new HashSet<>();
		while (result.next()) {
			userLinks.add(new UserLink(result.getInt("uid"), result.getString("userInfo")));
		}
		return userLinks;
	}
}
