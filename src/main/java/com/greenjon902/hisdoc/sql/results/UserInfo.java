package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record UserInfo(int uid, @NotNull UserData data, @NotNull java.util.Map<TagLink, Integer> countedTagLinks, int postCount,
					   int eventCount, @NotNull java.util.List<EventLink> recentEventLinks) {
	public UserInfo(int uid, @NotNull UserData data, @NotNull Map<TagLink, Integer> countedTagLinks, int postCount, int eventCount, @NotNull List<EventLink> recentEventLinks) {
		this.uid = uid;
		this.data = data;
		this.countedTagLinks = Collections.unmodifiableMap(countedTagLinks);
		this.postCount = postCount;
		this.eventCount = eventCount;
		this.recentEventLinks = Collections.unmodifiableList(recentEventLinks);
	}
}
