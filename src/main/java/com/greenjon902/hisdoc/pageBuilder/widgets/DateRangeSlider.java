package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

// TODO: Auto fill last values from post contents

public class DateRangeSlider implements WidgetBuilder {
	private final Timestamp date1;
	private final Timestamp date2;

	public DateRangeSlider(Timestamp date1, Timestamp date2) {
		this.date1 = date1;
		this.date2 = date2;
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

		stream.write("<label for=\"start-date\">Date 1: </label>" +
				"<input type=\"date\" id=\"start-date\" name=\"start-date\" value=\"" + min + "\" min=\"" + min + "\" max=\"" + max + "\">" +
				"<br>" +
				"<label for=\"end-date\">Date 2: </label>" +
				"<input type=\"date\" id=\"end-date\" name=\"end-date\" value=\"" + max + "\" min=\"" + min + "\" max=\"" + max + "\">");
	}
}
