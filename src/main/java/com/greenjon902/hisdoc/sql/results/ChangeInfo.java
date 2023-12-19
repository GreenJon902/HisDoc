package com.greenjon902.hisdoc.sql.results;

import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;

public record ChangeInfo(@Nullable FlexiDateTime date, @Nullable PersonLink author, @NotNull String description) {

}
