package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;

import java.io.IOException;

/**
 * Ensure a cookie called "session" exists. If not create it and fill it with a UUID, then reload the page.
 */
public class EnsureSessionIdScript extends Script {
	public EnsureSessionIdScript(PageBuilder pageBuilder) {
		pageBuilder.addScript(new CookieHelperScript());
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		stream.write("""
				if (getCookie("session") == null) {
					setCookie("session", crypto.randomUUID());
					location.reload();
				}
				""");  // Reload page to ensure server has the correct session id
	}
}
