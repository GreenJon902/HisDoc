package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public abstract class Script {
	public void writeTo(HtmlOutputStream stream) throws IOException {
		stream.write("<script>");
		writeScriptContents(stream);
		stream.write("</script>");
	}

	protected abstract void writeScriptContents(HtmlOutputStream stream) throws IOException;
}
