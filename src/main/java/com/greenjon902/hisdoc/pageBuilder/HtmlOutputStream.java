package com.greenjon902.hisdoc.pageBuilder;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import javax.swing.text.html.HTML;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/** A wrapper for an output stream that makes writing html easier*/
public class HtmlOutputStream {
	private final OutputStream outputStream;

	public HtmlOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * Write the exact text in string to the output stream, this will not replace any characters with html safe ones.
	 * See {@link #writeSafe(String string)}
	 * @param string The text to write.
	 */
	public void write(String string) throws IOException {
		outputStream.write(string.getBytes());
	}

	/**
	 * Writes the text in string to the output stream, this will replace any unsafe html characters with the correct
	 * versions.
	 * @param string The text to write.
	 */
	public void writeSafe(String string) throws IOException {
		write(escapeHtml4(string));
	}
}
