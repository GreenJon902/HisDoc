package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record PersonInfo(int id, @NotNull PersonData data, @NotNull java.util.Map<TagLink, Integer> countedTagLinks, int postCount,
					   int eventCount, @NotNull java.util.List<EventLink> recentEvents, @NotNull java.util.List<EventLink> recentPosts) implements Idable {
	public PersonInfo(int id, @NotNull PersonData data, @NotNull Map<TagLink, Integer> countedTagLinks, int postCount, int eventCount, @NotNull List<EventLink> recentEvents, @NotNull List<EventLink> recentPosts) {
		this.id = id;
		this.data = data;
		this.countedTagLinks = Collections.unmodifiableMap(countedTagLinks);
		this.postCount = postCount;
		this.eventCount = eventCount;
		this.recentEvents = Collections.unmodifiableList(recentEvents);
		this.recentPosts = Collections.unmodifiableList(recentPosts);
	}
}