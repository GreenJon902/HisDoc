package com.greenjon902.hisdoc.runners.papermc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConfigLoader {
	private final File dataFolder;

	public ConfigLoader(File dataFolder) {
		this.dataFolder = dataFolder;
	}

	public String get(ConfigItem item) throws IOException {
		File file = ensureExists(item);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		char[] contentBytes = new char[(int) file.length()];
		bufferedReader.read(contentBytes);
		return new String(contentBytes);
	}

	private File ensureExists(ConfigItem item) throws IOException {
		File file = new File(dataFolder, item.filename);
		dataFolder.mkdir();
		file.createNewFile();
		return file;
	}

	enum ConfigItem {
		MYSQL_HOST("mysql-host.txt"), MYSQL_USER("mysql-user.txt"), WEBDRIVER_PORT("webdriver-port.txt"),
		MYSQL_PASSWORD("mysql-password.txt"), ADD_EVENT_URL("add-event-url.txt");

		public final String filename;

		ConfigItem(String filename) {
			this.filename = filename;
		}
	}
}
