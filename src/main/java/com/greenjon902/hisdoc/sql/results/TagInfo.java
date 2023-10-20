package com.greenjon902.hisdoc.sql.results;

import java.util.List;

public record TagInfo(int tid, String name, String description, int color, List<EventLink> recentEvents) {
}
