package com.greenjon902.hisdoc.webDriver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Stream;

public class WebDriver {
	private final HttpServer server;
	protected final WebDriverConfig webDriverConfig;

	public WebDriver(WebDriverConfig webDriverConfig) throws IOException {
		this.webDriverConfig = webDriverConfig;

		server = HttpServer.create();
		server.bind(new InetSocketAddress(webDriverConfig.port()), webDriverConfig.backlog());

		for (String path : webDriverConfig.pageRenderers().keySet()) {
			server.createContext(path, new HttpHandlerImpl(webDriverConfig.pageRenderers().get(path)));
		}
		server.createContext("/favicon.ico", new FaviconHandler(webDriverConfig.favicon()));
	}

	/**
	 * Starts the HttpServer.
	 */
	public void start() {
		server.start();
	}

	/**
	 * Stops the HttpServer.
	 */
	public void stop() {
		server.stop(webDriverConfig.stopDelay());
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
		exchange.sendResponseHeaders(200, image.length);
		OutputStream os = exchange.getResponseBody();
		os.write(image);
		os.close();
	}
}

class HttpHandlerImpl implements HttpHandler {
	private final PageRenderer pageRenderer;

	public HttpHandlerImpl(PageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		User user = getUser(exchange);

		String rendered;
		try {
			Map<String, String> query = getQuery(exchange);
			rendered = pageRenderer.render(query, null, user);

		} catch (Exception e) {
			e.printStackTrace();
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

	public User getUser(HttpExchange exchange) {
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
				throw new RuntimeException("Expected cookie in two parts, got " + parts.length + ", " + Arrays.toString(parts));
			}
			switch (parts[0]) {
				case "theme" -> theme = parts[1];
				default -> otherCookies.put(parts[0], parts[1]);
			}
		}
		return new User(theme, otherCookies, exchange.getRemoteAddress());
	}
}