package com.greenjon902.hisdoc.webDriver;

import com.greenjon902.hisdoc.Main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

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

class HttpHandlerImpl implements HttpHandler {
	private final PageRenderer pageRenderer;

	public HttpHandlerImpl(PageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		String rendered;
		try {
			Map<String, String> query = getQuery(exchange);
			rendered = pageRenderer.render(query, null);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String error = sw.toString();

			exchange.sendResponseHeaders(200, error.length());
			OutputStream os = exchange.getResponseBody();
			os.write(error.getBytes());
			os.close();
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
}