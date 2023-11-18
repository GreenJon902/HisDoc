package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

// TODO: Auto fill last values from post contents

public class DateSelector implements WidgetBuilder {
	private final Timestamp date1;
	private final Timestamp date2;
	private final Timestamp defaultDate;
	private final String id;
	private final String updateFunc;

	public DateSelector(Timestamp date1, Timestamp date2, boolean defaultIsEarliest, String id, @Nullable String updateFunc) {
		if (date1.compareTo(date2) > 0) {
			Timestamp t = date1;
			date1 = date2;
			date2 = t;
		}

		this.date1 = date1;
		this.date2 = date2;
		this.defaultDate = defaultIsEarliest ? date1 : date2;
		this.id = id;
		this.updateFunc = updateFunc;
	}

	public DateSelector(Timestamp date1, Timestamp date2, Timestamp defaultDate, String id, @Nullable String updateFunc) {
		if (date1.compareTo(date2) > 0) {
			Timestamp t = date1;
			date1 = date2;
			date2 = t;
		}

		this.date1 = date1;
		this.date2 = date2;
		this.defaultDate = defaultDate;
		this.id = id;
		this.updateFunc = updateFunc;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		String min = date1.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String max = date2.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String defaultDate = this.defaultDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		stream.write("<input type=\"date\" id=\"" + id + "\" value=\"" + defaultDate + "\" " +
				"min=\"" + min + "\" max=\"" + max + "\" " +
				(updateFunc != null ? " onchange=\"" + updateFunc + "\"" : "") + ">");
	}
}
