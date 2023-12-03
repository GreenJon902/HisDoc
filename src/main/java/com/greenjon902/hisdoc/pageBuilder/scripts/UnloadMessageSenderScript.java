package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class UnloadMessageSenderScript extends Script {
	private final String message;
	private final String unlessFormId;

	public UnloadMessageSenderScript(String message) {
		this(message, null);
	}

	public UnloadMessageSenderScript(@NotNull String message, @Nullable String unlessFormId) {
		this.message = message;
		this.unlessFormId = unlessFormId;
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		stream.write("window.onbeforeunload = function(){\nreturn '");
		stream.write(message);
		stream.write("';\n};\n");
		if (unlessFormId != null) {
			stream.write("document.getElementById('");
			stream.write(unlessFormId);
			stream.write("').addEventListener('submit', async function() {\n" +
						"	window.onbeforeunload = null;\n" +
					    "   await new Promise(r => setTimeout(r, 100));\n" +
						"	document.body.innerHTML = '<p>This form has expired, please reload</p>';\n" +
					"});");
		}
	}
}
