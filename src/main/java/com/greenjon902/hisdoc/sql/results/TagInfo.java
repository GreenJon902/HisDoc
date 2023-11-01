package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TagInfo(int tid, @NotNull String name, @NotNull String description, int color, @NotNull List<EventLink> recentEvents) {
}
