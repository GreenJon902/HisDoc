package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class FormBuilder extends AbstractContainerWidgetBuilder {
	private final String id;

	public FormBuilder(@Nullable String id) {
		this.id = id;
	}

	public FormBuilder() {
		this(null);
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<form onkeydown=\"return event.key != 'Enter';\"");
		if (id != null) stream.write( " id=\"" + id + "\"");
		stream.write(">");
		renderAllChildren(stream, user);
		stream.write("</form>");
	}

	public static class SubmitButtonBuilder implements WidgetBuilder {
		@Override
		public void render(HtmlOutputStream stream, User user) throws IOException {
			stream.write("<input type=\"submit\" value=\"Submit\">");
		}
	}

	/**
	 * Builds a text input field, this can be an input with type "text" if a pattern is set, or a textarea if none is set.
	 * Note that due to HTML limitations, you cannot have both a pattern and multiple rows.
	 */
	public static class TextInputBuilder implements WidgetBuilder {
		private final String name;
		private final int rows;
		private final String pattern;

		public TextInputBuilder(String name, int rows) {
			this(name, rows, "");
		}

		public TextInputBuilder(String name, int rows, @NotNull String pattern) {
			this.name = name;
			this.rows = rows;
			this.pattern = pattern;

			if (!pattern.isEmpty() && rows != 1) {
				System.out.println("Text builder cannot have a multi row text input when a pattern is set!");
			}
		}

		@Override
		public void render(HtmlOutputStream stream, User user) throws IOException {
			stream.write("<");
			if (pattern.isEmpty()) {
				stream.write("textarea");
			} else {
				stream.write("input type=\"text\"");
			}

			stream.write(" class=\"text-input\" name=\"" + name + "\" rows=\"" + rows + "\"");
			if (!pattern.isEmpty()) {
				stream.write(" pattern=\"");
				stream.write(pattern);
				stream.write("\"");
			}
			stream.write(">");
			if (pattern.isEmpty()) {
				stream.write("</textarea>");
			} else {
				stream.write("</input>");
			}

		}
	}
}
