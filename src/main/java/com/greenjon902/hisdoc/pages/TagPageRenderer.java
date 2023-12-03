package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.ContainerWidgetBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.WidgetBuilder;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.TagInfo;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

public class TagPageRenderer extends PageRenderer {
	private final Dispatcher dispatcher;

	public TagPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		if (!query.containsKey("id")) {
			return "No id given :(";
		}

		int tagId;
		try {
			tagId = Integer.parseInt(query.get("id"));
		} catch (NumberFormatException e) {
			return "Malformed tag id :(\nTID: " + query.get("id");
		}
		TagInfo tagInfo = dispatcher.getTagInfo(tagId);

		if (tagInfo == null) {
			return "No tag found :(\nTID: " + tagId + " (" + query.get("id") + ")";
		}

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title(tagInfo.name());

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ContainerWidgetBuilder titleContainer = new ContainerWidgetBuilder("tag-page-title", "--circle-color: #" + String.format("%06x", tagInfo.color()));
		TextBuilder titleBuilder = new TextBuilder(TITLE);
		titleBuilder.add(tagInfo.name());
		titleContainer.add(titleBuilder);
		pageBuilder.add(titleContainer);

		TextBuilder idTextBuilder = new TextBuilder(MISC);
		idTextBuilder.add("TID: " + tagInfo.id());
		pageBuilder.add(idTextBuilder);

		TextBuilder descriptionTitleBuilder = new TextBuilder(SUBTITLE);
		descriptionTitleBuilder.add("Description");
		pageBuilder.add(descriptionTitleBuilder);
		TextBuilder descriptionTextBuilder = new TextBuilder(NORMAL);
		descriptionTextBuilder.add(tagInfo.description());
		pageBuilder.add(descriptionTextBuilder);

		TextBuilder recentEventsTitle = new TextBuilder(SUBTITLE);
		recentEventsTitle.add("Recent Events");
		pageBuilder.add(recentEventsTitle);
		WidgetBuilder recentEvents = EventPageRenderer.makeRecentEventContents(tagInfo.recentEvents());
		pageBuilder.add(recentEvents);

		return pageBuilder.render(user);
	}
}
