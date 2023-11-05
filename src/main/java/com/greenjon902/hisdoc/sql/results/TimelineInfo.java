package com.greenjon902.hisdoc.sql.results;

import java.util.Collections;
import java.util.List;

public record TimelineInfo(java.util.List<EventLink> eventLinks, java.util.Set<TagLink> tagLinks, java.util.Set<UserLink> userLinks) {
	public TimelineInfo(List<EventLink> eventLinks, java.util.Set<TagLink> tagLinks, java.util.Set<UserLink> userLinks) {
		this.eventLinks = Collections.unmodifiableList(eventLinks);
		this.tagLinks = Collections.unmodifiableSet(tagLinks);
		this.userLinks = Collections.unmodifiableSet(userLinks);
	}
}
