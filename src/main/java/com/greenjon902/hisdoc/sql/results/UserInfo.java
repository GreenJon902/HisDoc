package com.greenjon902.hisdoc.sql.results;

// TODO: All of these need maps and lists and sets to be unmodifiable


public record UserInfo(int uid, String userInfo, java.util.Map<TagLink, Integer> countedTagLinks, int postCount,
					   int eventCount, java.util.List<EventLink> recentEventLinks) {
}
