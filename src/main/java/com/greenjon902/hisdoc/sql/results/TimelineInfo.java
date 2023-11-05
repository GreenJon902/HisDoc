package com.greenjon902.hisdoc.sql.results;

import java.util.Collections;
import java.util.List;

public record TimelineInfo(java.util.List<EventLink> eventLinks) {
	public TimelineInfo(List<EventLink> eventLinks) {
		this.eventLinks = Collections.unmodifiableList(eventLinks);
	}
}
