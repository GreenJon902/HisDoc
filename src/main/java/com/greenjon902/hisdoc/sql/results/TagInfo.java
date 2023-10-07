package com.greenjon902.hisdoc.sql.results;

public record TagInfo(String name, int color) {
	public static TagInfo[] fromArrays(String[] tagNames, int[] tagColors) {
		if (tagNames.length != tagColors.length) {
			throw new RuntimeException("TagName and TagLength arrays should be of the same length");
		}
		TagInfo[] tagInfos = new TagInfo[tagNames.length];
		for (int i=0; i<tagInfos.length; i++) {
			tagInfos[i] = new TagInfo(tagNames[i], tagColors[i]);
		}
		return tagInfos;
	}
}
