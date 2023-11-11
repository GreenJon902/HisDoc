package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

// TODO: Auto fill last values from post contents

public class DateSelector implements WidgetBuilder {
	private final Timestamp date1;
	private final Timestamp date2;
	private final boolean defaultIsEarliest;
	private final String id;
	private final String updateFunc;

	public DateSelector(Timestamp date1, Timestamp date2, boolean defaultIsEarliest, String id, @Nullable String updateFunc) {
		this.date1 = date1;
		this.date2 = date2;
		this.defaultIsEarliest = defaultIsEarliest;
		this.id = id;
		this.updateFunc = updateFunc;
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		String min = date1.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String max = date2.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		if (date1.compareTo(date2) > 0) {
			String t = max;
			max = min;
			min = t;
		}

		String defaultDate = defaultIsEarliest ? min : max;

		stream.write("<input type=\"date\" id=\"" + id + "\" value=\"" + defaultDate + "\" " +
				"min=\"" + min + "\" max=\"" + max + "\" " +
				(updateFunc != null ? " onchange=\"" + updateFunc + "\"" : "") + ">");
	}
}
