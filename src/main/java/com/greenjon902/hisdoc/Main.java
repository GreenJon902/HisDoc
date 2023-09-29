package com.greenjon902.hisdoc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class Main {


	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			var h = t.getRequestHeaders();
			System.out.println(t.getRequestHeaders().get("cookie"));
			try {
			String response = " <form action=\"/action_page.php\">\n" +
					"  <label for=\"fname\">First name:</label><br>\n" +
					"  <input type=\"text\" id=\"fname\" name=\"fname\" value=\"John\"><br>\n" +
					"  <label for=\"lname\">Last name:</label><br>\n" +
					"  <input type=\"text\" id=\"lname\" name=\"lname\" value=\"Doe\"><br><br>\n" +
					"  <input type=\"submit\" value=\"Submit\">\n" +
					"</form> ";
			t.getResponseHeaders().set("Set-Cookie", "id=a3fWa;");



				System.out.println(Arrays.toString(t.getRequestBody().readAllBytes()));

			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();

			System.out.println(t.getRequestURI());
			System.out.println(t.getRequestMethod());
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
}
