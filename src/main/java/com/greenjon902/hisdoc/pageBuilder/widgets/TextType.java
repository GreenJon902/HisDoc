package com.greenjon902.hisdoc.pageBuilder.widgets;

import org.jetbrains.annotations.Nullable;

public enum TextType {
	TITLE("h1", "title", false),
	MAJOR_SUBTITLE("h2", "major-subtitle", true),
	SUBTITLE("h3", "subtitle", true),
	AUX_INFO_TITLE("h3", "auxiliary-info-title", false),
	NORMAL("p", "text", false),
	WARNING("p", "warning-text", false, "<i class=\"fa-solid fa-triangle-exclamation\"></i> "),
	MISC("p", "misc-text", false),
	CODE("p", "code", false);

	public final String tagType;
	public final String cssClass;
	public final String prefix;
	public final boolean separator;

	TextType(String tagType, String cssClass, boolean separator) {
		this(tagType, cssClass, separator, null);
	}

	TextType(String tagType, String cssClass, boolean separator, @Nullable String prefix) {
		this.tagType = tagType;
		this.cssClass = cssClass;
		this.separator = separator;
		this.prefix = prefix;
	}
}