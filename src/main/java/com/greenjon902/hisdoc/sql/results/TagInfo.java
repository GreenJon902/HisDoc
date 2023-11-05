package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public record TagInfo(int tid, @NotNull String name, @NotNull String description, int color, @NotNull List<EventLink> recentEvents) {
	public TagInfo(int tid, @NotNull String name, @NotNull String description, int color, @NotNull List<EventLink> recentEvents) {
		this.tid = tid;
		this.name = name;
		this.description = description;
		this.color = color;
		this.recentEvents = Collections.unmodifiableList(recentEvents);
	}
}
