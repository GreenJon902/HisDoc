package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.scripts.ThemeSwitcherHelperScript;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

/**
 * Actual styling is done in css, css also stores the next theme to go to, so I don't have to do state things.
 */
public class ThemeSwitcherBuilder implements WidgetBuilder {
	public ThemeSwitcherBuilder(PageBuilder pageBuilder) {
		pageBuilder.addScript(new ThemeSwitcherHelperScript(pageBuilder));
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<div id=\"theme-switcher-button\" class=\"theme-switcher-button\" onclick=\"cycleThemes()\">" +
							"<div id=\"theme-switcher-icon\"></div>" +
						"</div>");
	}
}
