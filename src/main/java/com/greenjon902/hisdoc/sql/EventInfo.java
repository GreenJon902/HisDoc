package com.greenjon902.hisdoc.sql;

public record EventInfo(String name, String description, String dateString, TagInfo[] tags, LinkInfo[] relatedEvents,
						LinkInfo[] relatedPlayers, Changelog[] changelogs, String createdDate, String postedBy) {
}
