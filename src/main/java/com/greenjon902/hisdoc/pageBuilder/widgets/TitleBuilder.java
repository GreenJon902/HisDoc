package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public class TitleBuilder extends AbstractTextBuilder {
	private TitleType type;

	public TitleBuilder(TitleType type) {
		this.type = type;
	}

	public TitleBuilder() {
		this(TitleType.NORMAL);
	}

	public static TitleBuilder subtitleBuilder() {
		return new TitleBuilder(TitleType.SUB);
	}
	public static TitleBuilder auxiliaryInfoTitleBuilder() {
		return new TitleBuilder(TitleType.AUXILIARY_INFO);
	}

	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<h");
		stream.write(String.valueOf(type.importance));
		stream.write(" class=\"");
		stream.write(type.cssClass);
		stream.write("\">");
		renderAllChildren(stream);
		stream.write("</h");
		stream.write(String.valueOf(type.importance));
		stream.write(">");
		if (type.separator) new SeparatorBuilder(0.2).render(stream);
	}
	
	public enum TitleType {
		NORMAL(1, "title", false), SUB(2, "subtitle", true), AUXILIARY_INFO(2, "auxiliary-info-title", false);

		private final int importance;
		private final String cssClass;
		private final boolean separator;

		TitleType(int importance, String cssClass, boolean separator) {
			this.importance = importance;
			this.cssClass = cssClass;
			this.separator = separator;
		}
	}
}
