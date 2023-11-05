package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public record EventInfo(int eid, String name, String description, Timestamp postedDate, UserLink postedBy,
						DateInfo eventDateInfo, Set<TagLink> tagLinks, Set<UserLink> relatedPlayerInfos,
						List<ChangeInfo> changeInfos, Set<EventLink> relatedEventLinks, String details) {
	public EventInfo(int eid, @NotNull String name, @NotNull String description, @Nullable Timestamp postedDate,
					 @Nullable UserLink postedBy, @NotNull DateInfo eventDateInfo, @NotNull Set<TagLink> tagLinks,
					 @NotNull Set<UserLink> relatedPlayerInfos, @NotNull List<ChangeInfo> changeInfos,
					 @NotNull Set<EventLink> relatedEventLinks, @Nullable String details) {
		this.eid = eid;
		this.name = name;
		this.description = description;
		this.eventDateInfo = eventDateInfo;
		this.tagLinks = Collections.unmodifiableSet(tagLinks);
		this.relatedPlayerInfos = Collections.unmodifiableSet(relatedPlayerInfos);
		this.changeInfos = Collections.unmodifiableList(changeInfos);
		this.postedDate = postedDate;
		this.postedBy = postedBy;
		this.relatedEventLinks = relatedEventLinks;
		this.details = details;
	}

	public EventInfo(int eid, String name, String description, DateInfo eventDateInfo, Set<TagLink> tagLinks, Set<UserLink> userLinks, List<ChangeInfo> changeInfos, Set<EventLink> relatedEventLinks, String details) {
		this(eid,name, description, null, null, eventDateInfo, tagLinks, userLinks, changeInfos, relatedEventLinks, details);
	}
}
