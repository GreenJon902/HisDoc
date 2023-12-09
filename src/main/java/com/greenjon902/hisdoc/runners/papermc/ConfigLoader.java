package com.greenjon902.hisdoc.runners.papermc;

import java.io.File;

public class ConfigLoader {
	private final File dataFolder;

	public ConfigLoader(File dataFolder) {
		this.dataFolder = dataFolder;
	}

	public void get(ConfigItem item) {

	}

	private void ensureExists(ConfigItem item) {

	}

	enum ConfigItem {
		MYSQL_HOST("mysql-host.txt");  THEN SET TO TESTING TO USE TEMPOARARY DB. SEND WARNING IN CONSOLE

				ALSO LINK UITest to use a custom runner too?
	}
}
