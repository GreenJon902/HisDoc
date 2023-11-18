package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
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
		stream.write("<form");
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

	public static class TextInputBuilder implements WidgetBuilder {
		private final String name;
		private final int rows;

		public TextInputBuilder(String name, int rows) {
			this.name = name;
			this.rows = rows;
		}

		@Override
		public void render(HtmlOutputStream stream, User user) throws IOException {
			stream.write("<textarea name=\"Text1\" rows=\"" + rows + "\"></textarea>");
		}
	}
}
