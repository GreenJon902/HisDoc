package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.pageBuilder.widgets.LogoBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.SeparatorBuilder;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;
import com.greenjon902.hisdoc.webDriver.WebDriver;
import com.greenjon902.hisdoc.webDriver.WebDriverConfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

public class UIPlayground {
	public static void main(String[] args) throws Exception {
		WebDriver webDriver = new WebDriver(new WebDriverConfig(
				Map.of("/", new PageRenderer() {
					@Override
					public String render(Map<String, String> query, String fragment, User session) throws SQLException {
						// Put builders here	------------------------------------------------------------------------
						PageBuilder pageBuilder = new PageBuilder();
						pageBuilder.add(new LogoBuilder());
						pageBuilder.add(new SeparatorBuilder(0.3));

						return pageBuilder.render(User.empty());
						// ---------------------------------------------------------------------------------------------
					}
				},
						"/themes", new PageRenderer() {

							@Override
							public String render(Map<String, String> query, String fragment, User session) throws SQLException {
								try {
									InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("com/greenjon902/hisdoc/pageBuilder/themes/" + query.get("name") + ".css");
									return new String(fileInputStream.readAllBytes());
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}
						}),
				8080, 0, 0,
				"com/greenjon902/hisdoc/logo.ico"), Logger.getLogger("HisDocUIPlayground"));
		webDriver.start();
	}
}
