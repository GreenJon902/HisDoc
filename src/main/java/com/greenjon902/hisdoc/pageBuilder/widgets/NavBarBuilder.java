package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.IOException;

public class NavBarBuilder implements WidgetBuilder {
	private ContainerWidgetBuilder navBar = new ContainerWidgetBuilder();

	public NavBarBuilder(PageBuilder pageBuilder) {
		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(new LogoBuilder());
		columnLayoutBuilder.add(new ThemeSwitcherBuilder(pageBuilder));

		navBar.add(columnLayoutBuilder);
		navBar.add(new SeparatorBuilder(0.3));
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		navBar.render(stream, session);
	}
}
