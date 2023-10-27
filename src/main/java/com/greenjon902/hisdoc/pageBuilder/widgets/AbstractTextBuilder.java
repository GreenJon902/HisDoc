package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public abstract class AbstractTextBuilder extends AbstractContainerWidgetBuilder {
	private String delimiter;

	public void delimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public AbstractTextBuilder(String delimiter) {
		this.delimiter = delimiter;
	}

	public AbstractTextBuilder() {
		this("");
	}

	public void add(String text) {
		super.add(new SimpleText(text + delimiter));
	}

	public void add(WidgetBuilder widgetBuilder) {
		super.add(widgetBuilder);
		if (!delimiter.isEmpty()) {
			super.add(new SimpleText(delimiter));
		}
	}

	/**
	 * @param color See {@link StyledText}.
	 * @param stylingFlags See {@link StyledText}.
	 */
	public void add(String text, int color, int stylingFlags) {
		add(new StyledText(text, color, stylingFlags));
	}

	/**
	 * @param stylingFlags See {@link StyledText}.
	 */
	public void add(String text, int stylingFlags) {
		add(text, -1, stylingFlags);
	}

	public void add(String text, String href) {
		add(new LinkedText(text, href));
	}
}

/**
 * A simple piece of text, it just renders as the text.
 * E.g. SimpleText("hello").render() = "hello"
 */
record SimpleText(String text) implements WidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.writeSafe(text);
	}
}

/**
 * A piece of text that acts like a html &lt;a> attribute.
 */
record LinkedText(String text, String href) implements WidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<a class=\"text-link\" href=\"");
		stream.writeSafe(href);
		stream.write("\">");
		stream.writeSafe(text);
		stream.write("</a>");
	}
}


/**
 * A styled piece of text, it renders as a span with inline style.
 * First bit of stylingFlags is bold, second is italic, third is underlined.
 * Color of -1 for no color.
 */
record StyledText(String text, int color, int stylingFlags) implements WidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<span style=\"");

		if (color != -1) stream.write("color: #" + String.format("%06x", color) + ";");
		if ((stylingFlags & 0b1) != 0) stream.write("font-weight: bold;");
		if ((stylingFlags & 0b10) != 0) stream.write("font-style: italic;");
		if ((stylingFlags & 0b100) != 0) stream.write("text-decoration: underline;");

		stream.write("\">");
		stream.writeSafe(text);
		stream.write("</span>");
	}
}
