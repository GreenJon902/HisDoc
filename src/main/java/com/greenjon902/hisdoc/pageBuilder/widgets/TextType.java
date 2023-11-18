package com.greenjon902.hisdoc.pageBuilder.widgets;

public enum TextType {
	TITLE("h1", "title", false),
	MAJOR_SUBTITLE("h2", "major-subtitle", true),
	SUBTITLE("h3", "subtitle", true),
	AUX_INFO_TITLE("h3", "auxiliary-info-title", false),
	NORMAL("p", "text", false),
	WARNING("p", "warning-text fas", false),
	MISC("p", "misc-text", false),
	CODE("p", "code", false);

	public final String tagType;
	public final String cssClass;
	public final boolean separator;

	TextType(String tagType, String cssClass, boolean separator) {
		this.tagType = tagType;
		this.cssClass = cssClass;
		this.separator = separator;
	}
}