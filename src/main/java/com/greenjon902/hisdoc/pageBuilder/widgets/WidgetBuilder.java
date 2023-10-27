package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface WidgetBuilder {
	void render(HtmlOutputStream stream) throws IOException;
}
