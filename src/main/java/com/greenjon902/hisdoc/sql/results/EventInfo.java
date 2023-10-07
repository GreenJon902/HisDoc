package com.greenjon902.hisdoc.sql.results;

public record EventInfo(int eid, String name, String description, DateInfo eventDateInfo, TagInfo[] tagInfos, Object userInfos, ChangeInfo[] changeInfos) {
}
