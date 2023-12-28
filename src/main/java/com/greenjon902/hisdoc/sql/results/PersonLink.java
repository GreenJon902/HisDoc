package com.greenjon902.hisdoc.sql.results;

import com.greenjon902.hisdoc.person.Person;
import org.jetbrains.annotations.NotNull;

public record PersonLink(int id, @NotNull Person person) implements Idable {
}
