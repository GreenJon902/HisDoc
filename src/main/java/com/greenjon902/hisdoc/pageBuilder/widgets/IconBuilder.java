package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

public class IconBuilder implements WidgetBuilder {

	private final IconType iconType;

	public IconBuilder(IconType iconType) {
		this.iconType = iconType;
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<i class=\"icon " + iconType.classes + "\"></i>");
	}

	public enum IconType {
		TIMELINE("fa-light fa-timeline"), PERSONS("fa-solid fa-users"), TAGS("fa-solid fa-tags");

		private final String classes;

		IconType(String classes) {
			this.classes = classes;
		}
	}
}
