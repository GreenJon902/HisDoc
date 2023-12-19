package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FormBuilder extends AbstractContainerWidgetBuilder {
	private final String id;
	private final Method method;
	private final String action;

	public FormBuilder(@Nullable String id, @NotNull Method method, String action) {
		this.id = id;
		this.method = method;
		this.action = action;
	}

	public FormBuilder(@NotNull Method method, String action) {
		this(null, method, action);
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<form onkeydown=\"return event.key != 'Enter';\" accept-charset=utf-8 method=\"" + method.string + "\" action=\"" + action + "\"");
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

	public enum Method {
		POST("POST"), GET("GET");

		public final String string;

		Method(String string) {
			this.string = string;
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
		private final String updateFunc;
		private final String defaultContents;

		public TextInputBuilder(String name, int rows) {
			this(name, rows, "");
		}

		public TextInputBuilder(String name, int rows, @NotNull String pattern, @Nullable String updateFunc, @NotNull String defaultContents) {
			this.name = name;
			this.rows = rows;
			this.pattern = pattern;
			this.updateFunc = updateFunc;
			this.defaultContents = defaultContents;

			if (!pattern.isEmpty() && rows != 1) {
				throw new IllegalArgumentException("Text builder cannot have a multi row text input when a pattern is set!");
			}
		}

		public TextInputBuilder(String name, int rows, @NotNull String pattern) {
			this(name, rows, "", "", "");
		}

		@Override
		public void render(HtmlOutputStream stream, User user) throws IOException {
			stream.write("<");
			if (pattern.isEmpty()) {
				stream.write("textarea");
			} else {
				stream.write("input type=\"text\"");
			}

			if (updateFunc != null) {
				stream.write(" oninput=\"" + updateFunc + "\"");
			}

			stream.write(" class=\"text-input\" name=\"" + name + "\" id=\"" + name + "\" rows=\"" + rows + "\"");
			if (!pattern.isEmpty()) {
				stream.write(" pattern=\"");
				stream.write(pattern);
				stream.write("\"");
			}
			stream.write(">");
			stream.write(defaultContents);
			if (pattern.isEmpty()) {
				stream.write("</textarea>");
			} else {
				stream.write("</input>");
			}

		}
	}

	public static class DateInfoInputBuilder implements WidgetBuilder {
		private final RadioButton dateType;

		public DateInfoInputBuilder() {

			this.dateType = new RadioButton("dateType", "Centered", List.of("Centered", "Ranged"),
					"""
								document.getElementById('datecLabel').hidden = this.value != 'Centered';
								document.getElementById('datec').hidden = this.value != 'Centered';
								document.getElementById('datecUnitsLabel').hidden = this.value != 'Centered';
								document.getElementById('datecUnits').hidden = this.value != 'Centered';
								document.getElementById('datecDiffLabel').hidden = this.value != 'Centered';
								document.getElementById('datecDiff').hidden = this.value != 'Centered';
								
								document.getElementById('daterLabel1').hidden = this.value != 'Ranged';
								document.getElementById('dater1').hidden = this.value != 'Ranged';
								document.getElementById('daterLabel2').hidden = this.value != 'Ranged';
								document.getElementById('dater2').hidden = this.value != 'Ranged';
							""");
		}

		@Override
		public void render(HtmlOutputStream stream, User user) throws IOException {
			dateType.render(stream, user);

			stream.write("<div style=\"display: grid;\">");

			stream.write("<input id=\"timezone\" name=\"timezone\" type=\"hidden\">");
			stream.write("<script>document.getElementById(\"timezone\").value = Intl.DateTimeFormat().resolvedOptions().timeZone;</script>");

			stream.write("<label id=\"datecLabel\" for=\"datec\">Center: </label>");
			stream.write("<input id=\"datec\" name=\"datec\" type=\"datetime-local\" step=\"60\" value=\"" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm")) + "\">");

			stream.write("""
						<label id="datecUnitsLabel" for="datecUnits">Units: </label>
						<select id="datecUnits" name="datecUnits">
						  <option value="m">Minutes</option>
						  <option value="h">Hours</option>
						  <option value="d">Days</option>
						</select>""");

			stream.write("<label id=\"datecDiffLabel\" for=\"datecDiff\">Diff: </label>");
			stream.write("<input id=\"datecDiff\" name=\"datecDiff\" type=\"number\" value=\"0\">");


			// Hide by default
			stream.write("<label id=\"daterLabel1\" for=\"dater1\" hidden>Start: </label>");
			stream.write("<input id=\"dater1\" name=\"dater1\" type=\"date\" value=\"" + LocalDate.now() + "\" hidden/>");
			stream.write("<label id=\"daterLabel2\" for=\"dater2\" hidden>End: </label>");
			stream.write("<input id=\"dater2\" name=\"dater2\" type=\"date\" value=\"" + LocalDate.now() + "\" hidden/>");
			stream.write("</div>");
		}
	}
}
