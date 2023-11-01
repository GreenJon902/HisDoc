package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

public record UserLink(int id, @NotNull String userInfo) {  // TODO: Decode userInfo
}
