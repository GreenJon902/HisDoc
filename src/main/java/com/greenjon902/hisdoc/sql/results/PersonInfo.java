package com.greenjon902.hisdoc.sql.results;

import com.greenjon902.hisdoc.person.Person;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record PersonInfo(int id, @NotNull Person person, @NotNull java.util.Map<TagLink, Integer> countedTagLinks, int postCount,
						 int eventCount, @NotNull java.util.List<EventLink> recentEvents, @NotNull java.util.List<EventLink> recentPosts) implements Idable {
	public PersonInfo(int id, @NotNull Person person, @NotNull Map<TagLink, Integer> countedTagLinks, int postCount, int eventCount, @NotNull List<EventLink> recentEvents, @NotNull List<EventLink> recentPosts) {
		this.id = id;
		this.person = person;
		this.countedTagLinks = Collections.unmodifiableMap(countedTagLinks);
		this.postCount = postCount;
		this.eventCount = eventCount;
		this.recentEvents = Collections.unmodifiableList(recentEvents);
		this.recentPosts = Collections.unmodifiableList(recentPosts);
	}
}
