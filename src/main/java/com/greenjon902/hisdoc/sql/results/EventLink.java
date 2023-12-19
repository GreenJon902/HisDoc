package com.greenjon902.hisdoc.sql.results;

import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import org.jetbrains.annotations.NotNull;


public record EventLink(int id, @NotNull String name, @NotNull FlexiDateTime dateInfo, @NotNull String description) implements Idable {
}
