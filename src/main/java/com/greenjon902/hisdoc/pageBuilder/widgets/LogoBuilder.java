package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class LogoBuilder implements WidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<span class=\"logo\">HisDoc</span>");
	}
}
