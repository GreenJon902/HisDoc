package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.*;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.NORMAL;

public class TagsPageRenderer extends HtmlPageRenderer {

	private final Dispatcher dispatcher;

	public TagsPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		ArrayList<TagLink> tagLinks = new ArrayList<>(dispatcher.getAllTagLinks());  // List so we can sort them
		tagLinks.sort(Comparator.comparing(o -> o.name().toLowerCase()));  // .toLowerCase() as sorting should be case-insensitive

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("Tags");

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		if (tagLinks.isEmpty()) {
			pageBuilder.add(new TextBuilder(NORMAL) {{
				add("No tags exist"); }});

		} else {
			ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");
			for (TagLink tagLink : tagLinks) {
				tagContainer.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color(), tagLink.description()));
			}
			pageBuilder.add(tagContainer);
			pageBuilder.add(new BreakBuilder());
		}

		return pageBuilder.render(user);
	}
}
