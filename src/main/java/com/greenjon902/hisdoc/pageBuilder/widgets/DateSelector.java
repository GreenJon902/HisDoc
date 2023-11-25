package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

// TODO: Auto fill last values from post contents

public class DateSelector implements WidgetBuilder {
	private final Timestamp defaultDate;
	private final String id;
	private final String updateFunc;

	public DateSelector(String id, @Nullable String updateFunc) {
		this(null, id, updateFunc);
	}

	public DateSelector(Timestamp defaultDate, String id, @Nullable String updateFunc) {
		this.defaultDate = defaultDate;
		this.id = id;
		this.updateFunc = updateFunc;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<input type=\"date\" id=\"" + id + "\"");
		if (defaultDate != null) {
			String defaultDate = this.defaultDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			stream.write(" value=\"" + defaultDate + "\"");
		}
		stream.write((updateFunc != null ? " onchange=\"" + updateFunc + "\"" : "") + ">");
	}
}
