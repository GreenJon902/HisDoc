package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class CssPageRenderer extends PageRenderer {
	@Override
	public String render(Map<String, String> query, String fragment, User user) {
		try {
			InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/pageBuilder/themes/" + query.get("name") + ".css");
			return new String(fileInputStream.readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String contentType() {
		return "text/css";
	}
}
