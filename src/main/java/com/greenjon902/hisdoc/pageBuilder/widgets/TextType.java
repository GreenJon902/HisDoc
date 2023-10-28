package com.greenjon902.hisdoc.pageBuilder.widgets;

public enum TextType {
	TITLE("h1", "title", false),
	SUBTITLE("h2", "subtitle", true),
	AUX_INFO_TITLE("h2", "auxiliary-info-title", false),
	NORMAL("p", "text", false),
	MISC("p", "misc-text", false);

	public final String tagType;
	public final String cssClass;
	public final boolean separator;

	TextType(String tagType, String cssClass, boolean separator) {
		this.tagType = tagType;
		this.cssClass = cssClass;
		this.separator = separator;
	}
}