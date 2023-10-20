package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;

public class LogoBuilder extends WidgetBuilder {
	@Override
	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<span class=\"logo\">HisDoc</span>");
	}
}
