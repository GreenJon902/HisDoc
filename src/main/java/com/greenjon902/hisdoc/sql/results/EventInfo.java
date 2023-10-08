package com.greenjon902.hisdoc.sql.results;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public record EventInfo(int eid, String name, String description, Timestamp postedDate, UserInfo postedBy, DateInfo eventDateInfo, Set<TagInfo> tagInfos, Set<UserInfo> userInfos, Set<ChangeInfo> changeInfos) {
	public EventInfo(int eid, String name, String description, Timestamp postedDate, UserInfo postedBy, DateInfo eventDateInfo, Set<TagInfo> tagInfos, Set<UserInfo> userInfos, Set<ChangeInfo> changeInfos) {
		this.eid = eid;
		this.name = name;
		this.description = description;
		this.eventDateInfo = eventDateInfo;
		this.tagInfos = Collections.unmodifiableSet(tagInfos);
		this.userInfos = Collections.unmodifiableSet(userInfos);
		this.changeInfos = Collections.unmodifiableSet(changeInfos);
		this.postedDate = postedDate;
		this.postedBy = postedBy;
	}

	public EventInfo(int eid, String name, String description, DateInfo eventDateInfo, Set<TagInfo> tagInfos, Set<UserInfo> userInfos, Set<ChangeInfo> changeInfos) {
		this(eid,name, description, null, null, eventDateInfo, tagInfos, userInfos, changeInfos);
	}
}
