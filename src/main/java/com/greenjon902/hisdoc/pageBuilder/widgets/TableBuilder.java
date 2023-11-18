package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableBuilder implements WidgetBuilder {
	private final int columnCount;
	private final boolean padding;
	private final List<WidgetBuilder> data = new ArrayList<>();

	public TableBuilder(int columnCount, boolean padding) {
		this.columnCount = columnCount;
		this.padding = padding;
	}

	public void add(WidgetBuilder widgetBuilder) {
		data.add(widgetBuilder);
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<table>");
		int n = 0;  // Column index on the current row
		for (WidgetBuilder cellBuilder : data) {
			if (n == 0) {
				stream.write("<tr>");
			}
			stream.write("<td");
			if (padding) {
				stream.write(" class=\"paddedTableColumn\"");
			}
			stream.write(">");
			cellBuilder.render(stream, user);
			stream.write("</td>");
			n += 1;
			if (n == columnCount) {
				stream.write("</tr>");
				n = 0;
			}
		}
		stream.write("</table>");
	}
}
