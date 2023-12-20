package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Sends files inside a specific folder based of the uri
public class FolderSenderHttpHandlerImpl implements HttpHandler {
	private final File folder;
	private final HashMap<String, byte[]> loadedFiles = new HashMap<>();

	public FolderSenderHttpHandlerImpl(File dataFolder) {
		super();
		this.folder = dataFolder;
	}

	private byte[] load(String path) {
		try {
			if (path.startsWith("/")) path = path.substring(1);
			File file = new File(folder, path);

			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String path = exchange.getRequestURI().getPath();
		if (path.endsWith("/")) path += "index.html";

		byte[] contents = loadedFiles.computeIfAbsent(path, this::load);

		exchange.sendResponseHeaders(200, 0);
		OutputStream os = exchange.getResponseBody();
		os.write(contents);
		os.close();

	}
}
