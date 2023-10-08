package com.greenjon902.hisdoc.webDriver;

import com.greenjon902.hisdoc.Main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
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
		getUser(exchange);

		Map<String, String> query = new HashMap<>();
		for (String line : exchange.getRequestURI().getQuery().split("\n")) {
			String[] sides = line.split("=");
			if (sides.length == 2) {
				query.put(sides[0], sides[1]);
			}
		}

		String rendered = "An error has occurred";
		try {
			rendered = pageRenderer.render(null, query, null);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}


		exchange.sendResponseHeaders(200, rendered.length());
		OutputStream os = exchange.getResponseBody();
		os.write(rendered.getBytes());
		os.close();
	}

	public void getUser(HttpExchange exchange) {
		List<String> cookies = exchange.getRequestHeaders().get("Cookie");
		System.out.println(cookies.get(0));
	}
}