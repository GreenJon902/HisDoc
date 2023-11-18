package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;

public record ChangeInfo(@Nullable Timestamp date, @Nullable PersonLink author, @NotNull String description) {

}
