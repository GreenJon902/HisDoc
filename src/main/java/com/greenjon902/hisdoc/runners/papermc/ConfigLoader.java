package com.greenjon902.hisdoc.runners.papermc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class ConfigLoader {
	private final File dataFolder;
	private final Logger logger;

	public ConfigLoader(File dataFolder, Logger logger) {
		this.dataFolder = dataFolder;
		this.logger = logger;
	}

	public String get(ConfigItem item) throws IOException {
		File file = ensureExists(item);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		char[] contentBytes = new char[(int) file.length()];
		bufferedReader.read(contentBytes);

		String contents = new String(contentBytes);
		if (item.strip) {
			contents = contents.strip();
		}

		if (item.print) {
			String finalContents = contents;
			logger.config(() -> "Loaded config item \"" + item + "\" is \"" + finalContents + "\"");
		} else {
			logger.config("Loaded config item \"" + item + "\"");
		}
		return contents;
	}

	private File ensureExists(ConfigItem item) throws IOException {
		File file = new File(dataFolder, item.filename);
		dataFolder.mkdir();
		file.createNewFile();
		return file;
	}

	enum ConfigItem {
		MYSQL_HOST("mysql-host.txt", true, true), MYSQL_USER("mysql-user.txt", true, true), WEBDRIVER_PORT("webdriver-port.txt", true, true),
		MYSQL_PASSWORD("mysql-password.txt", false, false), ADD_EVENT_URL("add-event-url.txt", true, true);

		public final String filename;
		private final boolean strip;
		private final boolean print;

		ConfigItem(String filename, boolean strip, boolean print) {
			this.filename = filename;
			this.strip = strip;
			this.print = print;
		}
	}
}
