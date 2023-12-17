package com.greenjon902.hisdoc.runners.papermc;


import java.util.logging.Formatter;
import java.util.logging.LogRecord;


import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Copied directly from {@link java.util.logging.SimpleFormatter} but made to use the default minecraft log format.
 */
public class LogFormatter extends Formatter {
	private final static String format = "[%1$tl:%1$tM:%1$tS %4$-7s] [%2$-32s] %5$s%6$s%n";

	@Override
	public String format(LogRecord record) {
		ZonedDateTime zdt = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());

		String source;
		if (record.getSourceClassName() != null) {
			source = record.getSourceClassName();
			if (record.getSourceMethodName() != null) {
				source += " " + record.getSourceMethodName();
			}
		} else {
			source = record.getLoggerName();
		}
		String[] splitSource = source.split("\\.");
		source = splitSource[splitSource.length - 1];


		String message = formatMessage(record);
		String throwable = "";
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			record.getThrown().printStackTrace(pw);
			pw.close();
			throwable = sw.toString();
		}
		return String.format(format,
				zdt,
				source,
				record.getLoggerName(),
				record.getLevel().getLocalizedName(),
				message,
				throwable);
	}
}
