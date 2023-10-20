package com.greenjon902.hisdoc.pageBuilder;

import ch.vorburger.mariadb4j.DB;
import com.greenjon902.hisdoc.pageBuilder.widgets.LogoBuilder;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

public class UIPlayground {
	public static void main(String[] args) throws Exception {
		WebDriver webDriver = new WebDriver(new WebDriverConfig(
				Map.of("/", new PageRenderer() {
					@Override
					public String render(Map<String, String> query, String fragment) throws SQLException {
						// Put builders here	------------------------------------------------------------------------
						PageBuilder pageBuilder = new PageBuilder();
						pageBuilder.add(new LogoBuilder());

						// TODO: Was remaking event page renderer, was next going to add a title widget for event name


						return pageBuilder.render();
						// ---------------------------------------------------------------------------------------------
					}
				},
						"/themes", new PageRenderer() {

							@Override
							public String render(Map<String, String> query, String fragment) throws SQLException {
								try {
									InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/pageBuilder/themes/" + query.get("name") + ".css");
									return new String(fileInputStream.readAllBytes());
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}
						}),
				8080, 0, 0
		));
		webDriver.start();
	}
}