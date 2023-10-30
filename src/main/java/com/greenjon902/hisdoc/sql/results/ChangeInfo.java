package com.greenjon902.hisdoc.sql.results;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public record ChangeInfo(Timestamp date, UserLink author, String description) {
	public static List<ChangeInfo> fromResultSet(ResultSet result) throws SQLException {
		List<ChangeInfo> changeInfos = new ArrayList<>();
		while (result.next()) {
			changeInfos.add(new ChangeInfo(result.getTimestamp("date"),
					new UserLink(result.getInt("authorUid"), result.getString("authorInfo")),
					result.getString("description")));
		}
		return changeInfos;
	}
}
