package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.flexiDateTime.CenteredFlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.RangedFlexiDate;
import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.greenjon902.hisdoc.flexiDateTime.CenteredFlexiDateTime.Units.*;
import static java.util.concurrent.TimeUnit.MINUTES;

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
		stream.write("<form accept-charset=utf-8 method=\"" + method.string + "\" action=\"" + action + "\"");
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
		private final boolean required;

		public TextInputBuilder(String name, int rows, String defaultContents, boolean required) {
			this(name, rows, "", defaultContents, required);
		}

		/**
		 * @param required True if this field can not be empty
		 */
		public TextInputBuilder(String name, int rows, @NotNull String pattern, @Nullable String updateFunc, @NotNull String defaultContents, boolean required) {
			this.name = name;
			this.rows = rows;
			this.pattern = pattern;
			this.updateFunc = updateFunc;
			this.defaultContents = defaultContents;
			this.required = required;

			if (!pattern.isEmpty() && rows != 1) {
				throw new IllegalArgumentException("Text builder cannot have a multi row text input when a pattern is set!");
			}
		}

		public TextInputBuilder(String name, int rows, @NotNull String pattern, @Nullable String updateFunc, @NotNull String defaultContents) {
			this(name, rows, pattern, updateFunc, defaultContents, false);
		}

		public TextInputBuilder(String name, int rows, @NotNull String pattern, String defaultContents, boolean required) {
			this(name, rows, pattern, "", defaultContents, required);
		}

		@Override
		public void render(HtmlOutputStream stream, User user) throws IOException {
			stream.write("<");
			if (pattern.isEmpty()) {
				stream.write("textarea");
			} else {
				stream.write("input type=\"text\" value=\"");
				stream.writeSafe(defaultContents);
				stream.write("\"");
			}

			if (updateFunc != null) {
				stream.write(" oninput=\"" + updateFunc + "\"");
			}

			stream.write(" class=\"text-input\" name=\"" + name + "\" id=\"" + name + "\" rows=\"" + rows + "\"");
			if (!pattern.isEmpty()) {
				stream.write(" pattern=\"");
				stream.write(pattern);
				stream.write("\" ");
				stream.write("onkeydown=\"return event.key != 'Enter';\"");  // As pattern means one line, but don't want to accidentally submit
			}
			if (required) stream.write(" required");
			stream.write(">");
			if (pattern.isEmpty()) {
				stream.write(defaultContents);
				stream.write("</textarea>");
			}

		}
	}

	public static class FlexiDateTimeInputBuilder implements WidgetBuilder {
		private final RadioButton dateType;
		private final FlexiDateTime defaultContents;

		public FlexiDateTimeInputBuilder() {
			this(null);
		}

		/**
		 * @param defaultContents The date to fill it with, if null then it uses the current date
		 */
		public FlexiDateTimeInputBuilder(@Nullable FlexiDateTime defaultContents) {
			this.defaultContents = defaultContents;

			String type;
			if (defaultContents == null || defaultContents instanceof CenteredFlexiDateTime) {  // Default value
				type = "Centered";
			} else if (defaultContents instanceof RangedFlexiDate) {
				type = "Ranged";
			} else {
				throw new RuntimeException("Unsupported FlexiDateTime type " + defaultContents);
			}
			System.out.println(type);

			this.dateType = new RadioButton("dateType", type, List.of("Centered", "Ranged"),
					List.of("", ""),  // TODO: Get actual descriptions?
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
			renderCentered(stream, defaultContents, defaultContents != null && !(defaultContents instanceof CenteredFlexiDateTime));  // Default option
			renderRanged(stream, defaultContents, !(defaultContents instanceof RangedFlexiDate));

			String dateOffset;
			if (defaultContents != null) {
				dateOffset = String.valueOf(defaultContents.offset());
			} else {
				dateOffset = "new Date().getTimezoneOffset();";  // Let JS figure it out
			}

			stream.write("<label for=\"offset\">The time offset from UTC: </label>");
			stream.write("<input id=\"offset\" name=\"offset\" type=\"text\"></input>"); // Text so we can have negative dates
			// Now set the default offset
			stream.write("<script>\n" +
					"let diff = " + dateOffset + "\n" + """
					let hours = "" + Math.trunc(diff / 60);
					if ((hours.includes("-") && hours.length) == 2 || hours.length == 1) {  // Ensure has two zeros
						// If negative, remove "-", then put a "0", and then add the "-" back/
						let newHours = "0" + Math.abs(Math.trunc(diff / 60));
						if (hours.includes("-")) {
							newHours = "-" + newHours;
						}
						hours = newHours;
					}
					let mins = "" + Math.abs(diff % 60);
					if (mins.length == 1) {
						mins = "0" + Math.abs(diff / 60);
					}
					document.getElementById("offset").value = hours + ":" + mins;
					</script>""");
			stream.write("<span>This should autofill correctly, if not (or if you entering a date from a different time zone) please check <a href=\"https://en.wikipedia.org/wiki/List_of_tz_database_time_zones\" target=\"_blank\">this list</a></span>");
			stream.write("</div>");
		}

		private static void renderCentered(HtmlOutputStream stream, FlexiDateTime defaultContents, boolean defaultHidden) throws IOException {
			String defaultCenter;
			CenteredFlexiDateTime.Units defaultUnits;
			long defaultDiff;

			if (defaultContents instanceof CenteredFlexiDateTime centeredFlexiDateTime) {  // instanceof does null comparison
				defaultUnits = centeredFlexiDateTime.units;
				defaultDiff = centeredFlexiDateTime.diff;

				String pattern =
						switch (defaultUnits) {
							case SECOND, MINUTE -> "yyyy-MM-dd HH:mm";
							case HOUR -> "yyyy-MM-dd HH:00";
							case DAY -> "yyyy-MM-dd 00:00";
						};
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				defaultCenter = centeredFlexiDateTime.formatCenter(format);

			} else {
				defaultCenter = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm"));
				defaultUnits = MINUTE;
				defaultDiff = 0;
			}

			String hiddenString = defaultHidden ? " hidden" : "";



			stream.write("<div style=\"display: grid;\" onkeydown=\"return event.key != 'Enter';\">");

			stream.write("<label id=\"datecLabel\" for=\"datec\"" + hiddenString + ">Center: </label>");
			stream.write("<input id=\"datec\" name=\"datec\" type=\"datetime-local\" step=\"60\" value=\"" + defaultCenter + "\"" + hiddenString + ">");

			stream.write("<label id=\"datecUnitsLabel\" for=\"datecUnits\"" + hiddenString + ">Units: </label>\n" +
					"<select id=\"datecUnits\" name=\"datecUnits\"" + hiddenString + ">\n" +
					"  <option value=\"m\"" + (defaultUnits == MINUTE ? " selected" : "") + ">Minutes</option>\n" +
					"  <option value=\"h\"" + (defaultUnits == HOUR ? " selected" : "") + ">Hours</option>\n" +
					"  <option value=\"d\"" + (defaultUnits == DAY ? " selected" : "") + ">Days</option>\n" +
					"</select>");

			stream.write("<label id=\"datecDiffLabel\" for=\"datecDiff\"" + hiddenString + ">Diff: </label>");
			stream.write("<input id=\"datecDiff\" name=\"datecDiff\" type=\"number\" value=\"" + defaultDiff + "\"" + hiddenString + ">");
		}

		private static void renderRanged(HtmlOutputStream stream, FlexiDateTime defaultContents, boolean defaultHidden) throws IOException {
			String date1;
			String date2;

			if (defaultContents instanceof RangedFlexiDate rangedFlexiDate) {  // instanceof does null comparison
				date1 = rangedFlexiDate.formatStart();
				date2 = rangedFlexiDate.formatEnd();

			} else {
				date1 = String.valueOf(LocalDate.now());
				date2 = String.valueOf(LocalDate.now());
			}

			String hiddenString = defaultHidden ? " hidden" : "";


			stream.write("<label id=\"daterLabel1\" for=\"dater1\" " + hiddenString + ">Start: </label>");
			stream.write("<input id=\"dater1\" name=\"dater1\" type=\"date\" value=\"" + date1 + "\" " + hiddenString + "/>");
			stream.write("<label id=\"daterLabel2\" for=\"dater2\" " + hiddenString + ">End: </label>");
			stream.write("<input id=\"dater2\" name=\"dater2\" type=\"date\" value=\"" + date2 + "\" " + hiddenString + "/>");
		}
	}
}
