package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;
import java.util.Objects;

/**
 * Note, because these are stored in a set, we can add duplicates and it should work just fine. So we can add a new script
 * to "require" it, and ignore that it may already exist.
 * // TODO: Add test for this
 */
public abstract class Script {
	public void writeTo(HtmlOutputStream stream) throws IOException {
		stream.write("<script>");
		writeScriptContents(stream);
		stream.write("</script>");
	}

	protected abstract void writeScriptContents(HtmlOutputStream stream) throws IOException;

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		return object != null && getClass() == object.getClass();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass());
	}
}
