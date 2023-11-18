package com.greenjon902.hisdoc.sql.results;

import java.util.*;

public record TimelineInfo(java.util.List<EventLink> eventLinks, java.util.Set<TagLink> tagLinks, java.util.Set<PersonLink> personLinks,
						   Map<EventLink, ArrayList<TagLink>> eventTagRelations,
						   Map<EventLink, ArrayList<PersonLink>> eventPersonRelations) {
	public TimelineInfo(List<EventLink> eventLinks, Set<TagLink> tagLinks, Set<PersonLink> personLinks, Map<EventLink, ArrayList<TagLink>> eventTagRelations, Map<EventLink, ArrayList<PersonLink>> eventPersonRelations) {
		this.eventLinks = Collections.unmodifiableList(eventLinks);
		this.tagLinks = Collections.unmodifiableSet(tagLinks);
		this.personLinks = Collections.unmodifiableSet(personLinks);
		this.eventTagRelations = Collections.unmodifiableMap(eventTagRelations);
		this.eventPersonRelations = Collections.unmodifiableMap(eventPersonRelations);
	}
}
