package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A widget that can take more widgets.
 */
public abstract class ContainerWidgetBuilder extends WidgetBuilder {
	protected final List<WidgetBuilder> childrenBuilders = new ArrayList<>();

	public void add(WidgetBuilder widgetBuilder) {
		childrenBuilders.add(widgetBuilder);
	}

	protected void renderAllChildren(HtmlOutputStream stream) throws IOException {
		for (WidgetBuilder childBuilder : childrenBuilders) {
			childBuilder.render(stream);
		}
	}
}
