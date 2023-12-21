package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.BreakBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.ContainerWidgetBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TagBuilder;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class TagsPageRenderer extends HtmlPageRenderer {

	private final Dispatcher dispatcher;

	public TagsPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		Set<TagLink> tagLinks = dispatcher.getAllTagLinks();

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("Tags");

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");
		for (TagLink tagLink : tagLinks) {
			tagContainer.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color(), tagLink.description()));
		}
		pageBuilder.add(tagContainer);
		pageBuilder.add(new BreakBuilder());

		return pageBuilder.render(user);
	}
}
