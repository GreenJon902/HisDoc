package com.greenjon902.hisdoc;

import com.greenjon902.hisdoc.runners.papermc.LogFormatter;

import java.util.logging.*;

public class Utils {
	public static Logger getTestLogger() {
		Logger logger = Logger.getLogger("TestingLogger");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);

		StdoutHandler consoleHandler = new StdoutHandler();
		consoleHandler.setFormatter(new LogFormatter());
		consoleHandler.setLevel(Level.ALL);
		logger.addHandler(consoleHandler);

		return logger;
	}
}

/**
 * Basically the {@link ConsoleHandler} but now intelliJ won't turn logs red. Which means red stuff is mySql, and not
 * red stuff is actually important.
 */
class StdoutHandler extends java.util.logging.StreamHandler {
	public StdoutHandler() {
		super();
		setOutputStream(System.out);

	}

	@Override
	public void publish(LogRecord record) {
		super.publish(record);
		flush();
	}

	@Override
	public void close() {
		flush();
	}
}