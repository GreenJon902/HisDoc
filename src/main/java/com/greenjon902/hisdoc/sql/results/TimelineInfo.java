package com.greenjon902.hisdoc.sql.results;

import java.util.*;

public record TimelineInfo(java.util.List<EventLink> eventLinks, java.util.Set<TagLink> tagLinks, java.util.Set<UserLink> userLinks,
						   Map<EventLink, ArrayList<TagLink>> eventTagRelations,
						   Map<EventLink, ArrayList<UserLink>> eventUserRelations) {
	public TimelineInfo(List<EventLink> eventLinks, Set<TagLink> tagLinks, Set<UserLink> userLinks, Map<EventLink, ArrayList<TagLink>> eventTagRelations, Map<EventLink, ArrayList<UserLink>> eventUserRelations) {
		this.eventLinks = Collections.unmodifiableList(eventLinks);
		this.tagLinks = Collections.unmodifiableSet(tagLinks);
		this.userLinks = Collections.unmodifiableSet(userLinks);
		this.eventTagRelations = Collections.unmodifiableMap(eventTagRelations);
		this.eventUserRelations = Collections.unmodifiableMap(eventUserRelations);
	}
}
