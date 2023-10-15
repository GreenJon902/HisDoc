package com.greenjon902.hisdoc.sql.results;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;

public record EventInfo(int eid, String name, String description, Timestamp postedDate, UserLink postedBy, DateInfo eventDateInfo, Set<TagLink> tagLinks, Set<UserLink> relatedPlayerInfos, Set<ChangeInfo> changeInfos) {
	public EventInfo(int eid, String name, String description, Timestamp postedDate, UserLink postedBy, DateInfo eventDateInfo, Set<TagLink> tagLinks, Set<UserLink> relatedPlayerInfos, Set<ChangeInfo> changeInfos) {
		this.eid = eid;
		this.name = name;
		this.description = description;
		this.eventDateInfo = eventDateInfo;
		this.tagLinks = Collections.unmodifiableSet(tagLinks);
		this.relatedPlayerInfos = Collections.unmodifiableSet(relatedPlayerInfos);
		this.changeInfos = Collections.unmodifiableSet(changeInfos);
		this.postedDate = postedDate;
		this.postedBy = postedBy;
	}

	public EventInfo(int eid, String name, String description, DateInfo eventDateInfo, Set<TagLink> tagLinks, Set<UserLink> userLinks, Set<ChangeInfo> changeInfos) {
		this(eid,name, description, null, null, eventDateInfo, tagLinks, userLinks, changeInfos);
	}
}