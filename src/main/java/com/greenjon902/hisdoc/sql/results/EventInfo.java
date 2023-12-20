package com.greenjon902.hisdoc.sql.results;

import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public record EventInfo(int id, String name, String description, FlexiDateTime postedDate, PersonLink postedBy,
						FlexiDateTime eventDateInfo, Set<TagLink> tagLinks, Set<PersonLink> relatedPlayerInfos,
						List<ChangeInfo> changeInfos, Set<EventLink> relatedEventLinks, String details) implements Idable{
	public EventInfo(int id, @NotNull String name, @NotNull String description, @Nullable FlexiDateTime postedDate,
					 @Nullable PersonLink postedBy, @NotNull FlexiDateTime eventDateInfo, @NotNull Set<TagLink> tagLinks,
					 @NotNull Set<PersonLink> relatedPlayerInfos, @NotNull List<ChangeInfo> changeInfos,
					 @NotNull Set<EventLink> relatedEventLinks, @Nullable String details) {
		this.id = id;
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

	public EventInfo(int eid, String name, String description, FlexiDateTime eventDateInfo, Set<TagLink> tagLinks, Set<PersonLink> personLinks, List<ChangeInfo> changeInfos, Set<EventLink> relatedEventLinks, String details) {
		this(eid,name, description, null, null, eventDateInfo, tagLinks, personLinks, changeInfos, relatedEventLinks, details);
	}
}
