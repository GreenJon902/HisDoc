package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.pageBuilder.widgets.*;
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
						pageBuilder.add(new SeparatorBuilder(0.3));

						// -------------------
						ContainerWidgetBuilder left = new ContainerWidgetBuilder();

						TitleBuilder titleBuilder = new TitleBuilder();
						titleBuilder.add("Event Name");
						left.add(titleBuilder);

						TitleBuilder descriptionTitleBuilder = TitleBuilder.subtitleBuilder();
						descriptionTitleBuilder.add("Description");
						left.add(descriptionTitleBuilder);
						TextBuilder descriptionTextBuilder = new TextBuilder();
						descriptionTextBuilder.add("This is an example of a paragraph for a description of this event.\nthat means it is very\n\npog\n and has line breaks and stuffs, it also works wtih <br> html tags by using some other library or somin i cant remember! :)");
						left.add(descriptionTextBuilder);

						TitleBuilder changelogTitleBuilder = TitleBuilder.subtitleBuilder();
						changelogTitleBuilder.add("Changelog");
						left.add(changelogTitleBuilder);
						TextBuilder changelogTextBuilder = new TextBuilder();
						changelogTextBuilder.add("2023/09/12  GreenJon: ");
						changelogTextBuilder.add("did your mother.\n", 0xaaaaaa, 0);
						changelogTextBuilder.add("2023/09/13  GreenJon: ");
						changelogTextBuilder.add("remembered asking\n", 0xaaaaaa, 0);
						changelogTextBuilder.add("2023/09/14  GreenJon: ");
						changelogTextBuilder.add("abouyt your mother\n", 0xaaaaaa, 0);
						left.add(changelogTextBuilder);
						// -------------------

						ContainerWidgetBuilder right = new ContainerWidgetBuilder();

						TitleBuilder tagTitles = TitleBuilder.auxiliaryInfoTitleBuilder();
						tagTitles.add("Tags");
						right.add(tagTitles);
						ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");
						tagContainer.add(new TagBuilder("Hello", 3, 0xff0000));
						tagContainer.add(new TagBuilder("Hi", 3, 0x00ff00));
						tagContainer.add(new TagBuilder("Who Asked", 3, 0x0000ff));
						right.add(tagContainer);
						right.add(new BreakBuilder());

						TitleBuilder relatedEventTitles = TitleBuilder.auxiliaryInfoTitleBuilder();
						relatedEventTitles.add("Related Events");
						right.add(relatedEventTitles);
						TextBuilder relatedEvents = new TextBuilder("\n");
						relatedEvents.add("a", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
						relatedEvents.add("bcdefghijklmnopqrstuvplaw", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
						relatedEvents.add("3w4aet gaerta eraerhyg a", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
						relatedEvents.add("3w4aet gaerta eraerhyg awawer g", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
						right.add(relatedEvents);
						right.add(new BreakBuilder());

						TitleBuilder relatedUserTitles = TitleBuilder.auxiliaryInfoTitleBuilder();
						relatedUserTitles.add("Related User");
						right.add(relatedUserTitles);
						TextBuilder relatedUsers = new TextBuilder("\n");
						relatedUsers.add("Me", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
						relatedUsers.add("you", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
						right.add(relatedUsers);
						// -------------------


						ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
						columnLayoutBuilder.add(left);
						columnLayoutBuilder.add(right);

						pageBuilder.add(columnLayoutBuilder);
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
