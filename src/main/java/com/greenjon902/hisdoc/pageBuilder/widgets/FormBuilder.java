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
		stream.write("<form onkeydown=\"return event.key != 'Enter';\" method=\"" + method.string + "\" action=\"" + action + "\"");
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

	public static class DateInfoInputBuilder implements WidgetBuilder {
		private final RadioButton dateType;

		public DateInfoInputBuilder() {

			this.dateType = new RadioButton("dateType", "Centered", List.of("Centered", "Between"),
					"""
       						console.log(this.value);
       						console.log(this.checked);
							
								console.log('yay');
								document.getElementById('datecLabel1').hidden = this.value != 'Centered';
								document.getElementById('datec1').hidden = this.value != 'Centered';
								document.getElementById('datecPrecisionLabel').hidden = this.value != 'Centered';
								document.getElementById('datecPrecision').hidden = this.value != 'Centered';
								document.getElementById('datecDiffLabel').hidden = this.value != 'Centered';
								document.getElementById('datecDiff').hidden = this.value != 'Centered';
								document.getElementById('datecDiffTypeLabel').hidden = this.value != 'Centered';
								document.getElementById('datecDiffType').hidden = this.value != 'Centered';
								
								document.getElementById('datebLabel1').hidden = this.value != 'Between';
								document.getElementById('dateb1').hidden = this.value != 'Between';
								document.getElementById('datebLabel2').hidden = this.value != 'Between';
								document.getElementById('dateb2').hidden = this.value != 'Between';
							""");
		}

		@Override
		public void render(HtmlOutputStream stream, User user) throws IOException {
			dateType.render(stream, user);

			stream.write("<div style=\"display: grid;\">");
			stream.write("<label id=\"datecLabel1\" for=\"datec1\">Center: </label>");
			stream.write("<input id=\"datec1\" name=\"datec1\" type=\"datetime-local\" value=\"" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")) + "\">");

			stream.write("""
						<label id="datecPrecisionLabel" for="datecPrecision">Precision: </label>
						<select id="datecPrecision" name="datecPrecision">
						  <option value="m">Minutes</option>
						  <option value="h">Hours</option>
						  <option value="d">Days</option>
						</select>""");

			stream.write("<label id=\"datecDiffLabel\" for=\"datecDiff\">Diff: </label>");
			stream.write("<input id=\"datecDiff\" name=\"datecDiff\" type=\"number\" value=\"0\">");
			stream.write("""
						<label id="datecDiffTypeLabel" for="datecDiffType">Diff Type: </label>
						<select id="datecDiffType" name="datecDiffType">
						  <option value="m">Minutes</option>
						  <option value="h">Hours</option>
						  <option value="d">Days</option>
						</select>""");


			// Hide by default
			stream.write("<label id=\"datebLabel1\" for=\"dateb1\" hidden>Start: </label>");
			stream.write("<input id=\"dateb1\" name=\"dateb1\" type=\"date\" value=\"" + LocalDate.now() + "\" hidden/>");
			stream.write("<label id=\"datebLabel2\" for=\"dateb2\" hidden>End: </label>");
			stream.write("<input id=\"dateb2\" name=\"dateb2\" type=\"date\" value=\"" + LocalDate.now() + "\" hidden/>");
			stream.write("</div>");
		}
	}
}
