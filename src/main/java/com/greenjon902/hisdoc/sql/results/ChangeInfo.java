package com.greenjon902.hisdoc.sql.results;

import java.sql.Timestamp;

public record ChangeInfo(Timestamp date, UserInfo author, String description) {
}
