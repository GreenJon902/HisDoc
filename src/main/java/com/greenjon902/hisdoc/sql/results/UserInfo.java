package com.greenjon902.hisdoc.sql.results;

// TODO: Wrap user info string in its own class to make getting types easier

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record UserInfo(int uid, @NotNull String userInfo, @NotNull java.util.Map<TagLink, Integer> countedTagLinks, int postCount,
					   int eventCount, @NotNull java.util.List<EventLink> recentEventLinks) {
	public UserInfo(int uid, @NotNull String userInfo, @NotNull Map<TagLink, Integer> countedTagLinks, int postCount, int eventCount, @NotNull List<EventLink> recentEventLinks) {
		this.uid = uid;
		this.userInfo = userInfo;
		this.countedTagLinks = Collections.unmodifiableMap(countedTagLinks);
		this.postCount = postCount;
		this.eventCount = eventCount;
		this.recentEventLinks = Collections.unmodifiableList(recentEventLinks);
	}
}
