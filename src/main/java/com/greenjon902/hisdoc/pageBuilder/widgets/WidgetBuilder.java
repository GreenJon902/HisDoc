package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class WidgetBuilder {
	public abstract void render(HtmlOutputStream stream) throws IOException;
}
