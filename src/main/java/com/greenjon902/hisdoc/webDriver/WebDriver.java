package com.greenjon902.hisdoc.webDriver;

import com.greenjon902.hisdoc.Main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class WebDriver {
	private final HttpServer server;
	protected final WebDriverConfig webDriverConfig;
	private final HttpHandlerImpl httpHandler;

	public WebDriver(WebDriverConfig webDriverConfig) throws IOException {
		this.webDriverConfig = webDriverConfig;
		this.httpHandler = new HttpHandlerImpl();

		server = HttpServer.create();
		server.bind(new InetSocketAddress(webDriverConfig.port()), webDriverConfig.backlog());

		server.createContext("/", httpHandler);
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
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		getUser(exchange);
		exchange.sendResponseHeaders(10, 10);
		exchange.close();

		System.out.println(exchange.getRequestURI().getQuery());
		System.out.println(exchange.getRequestHeaders());
		System.out.println(exchange.getRequestBody());
		System.out.println(exchange.getRequestURI());
		System.out.println(exchange.getRequestMethod());
	}

	public void getUser(HttpExchange exchange) {
		List<String> cookies = exchange.getRequestHeaders().get("Cookie");
		System.out.println(cookies.get(0));
	}
}