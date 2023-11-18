package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

public record PersonLink(int id, @NotNull PersonData data) implements Idable {
}
