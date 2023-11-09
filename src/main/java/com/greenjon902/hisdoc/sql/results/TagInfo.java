package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public record TagInfo(int id, @NotNull String name, @NotNull String description, int color, @NotNull List<EventLink> recentEvents) implements Idable {
	public TagInfo(int id, @NotNull String name, @NotNull String description, int color, @NotNull List<EventLink> recentEvents) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.color = color;
		this.recentEvents = Collections.unmodifiableList(recentEvents);
	}
}
