package com.greenjon902.hisdoc.webDriver;

import com.greenjon902.hisdoc.runners.papermc.FolderSenderHttpHandlerImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class WebDriver {
	private final HttpServer server;
	protected final WebDriverConfig webDriverConfig;
	private final Logger logger;

	public WebDriver(WebDriverConfig webDriverConfig, Logger logger) throws IOException {
		this.webDriverConfig = webDriverConfig;
		this.logger = logger;

		logger.finer("Binding HttpServer to" + webDriverConfig.port() + "...");
		server = HttpServer.create();
		server.bind(new InetSocketAddress(webDriverConfig.port()), webDriverConfig.backlog());

		logger.finer("Creating contexts...");
		for (String path : webDriverConfig.pageRenderers().keySet()) {
			server.createContext(path, new HttpHandlerImpl(webDriverConfig.pageRenderers().get(path), logger));
		}
		server.createContext("/favicon.ico", new FaviconHandler(webDriverConfig.favicon()));
	}

	/**
	 * Starts the HttpServer.
	 */
	public void start() {
		logger.fine("Starting HttpServer...");
		server.start();
	}

	/**
	 * Stops the HttpServer.
	 */
	public void stop() {
		logger.fine("Stopping HttpServer...");
		server.stop(webDriverConfig.stopDelay());
	}

	public void addHandler(String s, HttpHandler handler) {
		server.createContext(s, handler);
	}
}

class FaviconHandler implements HttpHandler {
	private final byte[] image;

	public FaviconHandler(String path) {
		try {
			InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(path);
			image = fileInputStream.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().set("Content-Type", "image/x-icon");
		exchange.sendResponseHeaders(200, image.length);
		OutputStream os = exchange.getResponseBody();
		os.write(image);
		os.close();
	}
}

class HttpHandlerImpl implements HttpHandler {
	private final PageRenderer pageRenderer;
	private final Logger logger;

	public HttpHandlerImpl(PageRenderer pageRenderer, Logger logger) {
		this.pageRenderer = pageRenderer;
		this.logger = logger;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		logger.finer("Got a request from \"" + exchange.getRemoteAddress().getHostString() + "\" for the page \"" + exchange.getRequestURI() + "\"");

		String rendered;
		try {
			User user = getUser(exchange);
			logger.finer(() -> "Request is from " + user);
			Map<String, String> query = getQuery(exchange);
			logger.finer(() -> "\twith the query " + query);

			exchange.getResponseHeaders().set("Content-Type", pageRenderer.contentType());
			rendered = pageRenderer.render(query, null, user);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			rendered = "Sorry, we experienced an error, please send this page to Jon\n\n" + sw;

			exchange.getResponseHeaders().set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200, rendered.length());
			OutputStream os = exchange.getResponseBody();
			os.write(rendered.getBytes());
			os.close();

			logger.log(Level.WARNING, "An error occurred while responding to a request", e);
			throw new RuntimeException(e);
		}


		exchange.sendResponseHeaders(200, rendered.length());
		OutputStream os = exchange.getResponseBody();
		os.write(rendered.getBytes());
		os.close();
	}

	protected Map<String, String> getQuery(HttpExchange exchange) {
		String queryString = exchange.getRequestURI().getQuery();

		if (queryString == null || queryString.isEmpty()) return Collections.emptyMap();

		Map<String, String> query = new HashMap<>();
		for (String line : queryString.split("&")) {
			String[] sides = line.split("=", 2);
			if (sides.length == 1) {
				query.put(sides[0], null);
			} else if (sides.length == 2) {
				query.put(sides[0], sides[1]);
			} else {
				throw new RuntimeException("The query item has the wrong number of parts, expected 1 or 2, got " + sides.length);
			}
		}
		return query;
	}

	public User getUser(HttpExchange exchange) throws IOException {
		// Post ---
		// Expect it to be encoded, so the & signs will only be there to separate key-value pairs
		String postString = new String(exchange.getRequestBody().readAllBytes());

		HashMap<String, String> post = new HashMap<>();
		if (!postString.isEmpty()) {
			String[] postStrings = postString.split("&");
			for (String string : postStrings) {
				String[] split = string.split("=", 2);
				if (split.length != 2) {
					throw new RuntimeException("Expected post entry in two parts, got " + split.length + ", " + Arrays.toString(split) + ", \"" + string + "\"");
				}
				Charset charset = StandardCharsets.UTF_8;
				post.put(URLDecoder.decode(split[0], charset), URLDecoder.decode(split[1], charset));
			}
		}


		// Cookies ---
		List<String> cookieStrings = exchange.getRequestHeaders().getOrDefault("Cookie", Collections.emptyList());

		List<String> cookies = new ArrayList<>();
		for (String cookieString : cookieStrings) {
			cookies.addAll(Stream.of(cookieString.split(";")).map(String::trim).toList());
		}

		HashMap<String, String> otherCookies = new HashMap<>();
		String theme = "";
		for (String cookie : cookies) {
			String[] parts = cookie.split("=", 2);
			if (parts.length != 2) {
				throw new RuntimeException("Expected cookie in two parts, got " + parts.length + ", " + Arrays.toString(parts) + ", \"" + cookie + "\"");
			}
			// Get cookies that will be used throughout the website into their own fields, page specific ones can be dealt with there
			switch (parts[0]) {
				case "theme" -> theme = parts[1];
				default -> {
					if (otherCookies.containsKey(parts[0])) throw new RuntimeException("Duplicate cookie key " + parts[0]);
					otherCookies.put(parts[0], parts[1]);
				}
			}
		}
		return new User(theme, otherCookies, exchange.getRemoteAddress(), post);
	}
}