package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;


public record EventLink(int id, @NotNull String name, @NotNull DateInfo dateInfo, @NotNull String description) implements Idable {
}
