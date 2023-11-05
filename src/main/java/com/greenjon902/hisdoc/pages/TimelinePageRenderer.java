package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventLink;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.sql.results.TimelineInfo;
import com.greenjon902.hisdoc.sql.results.UserLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.Session;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

// TODO: Add start and end timeline infos, also sort by posted/actual, also filtering, etc

public class TimelinePageRenderer extends PageRenderer {

	private final Dispatcher dispatcher;

	public TimelinePageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, Session session) throws SQLException {
		TimelineInfo timelineInfo = dispatcher.getTimelineInfo();

		if (timelineInfo == null) {
			return "No timeline info found?";
		}

		PageBuilder pageBuilder = new PageBuilder();
		LazyLoadAccountNameScript lazyLoadAccountNameScript = new LazyLoadAccountNameScript();  // Variables added elsewhere
		pageBuilder.addScript(lazyLoadAccountNameScript);

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ColumnLayoutBuilder column = new ColumnLayoutBuilder();
		column.add(makeLeft(timelineInfo));
		column.add(makeRight(timelineInfo, pageBuilder, lazyLoadAccountNameScript));
		pageBuilder.add(column);


		return pageBuilder.render(session);
	}

	private ContainerWidgetBuilder makeLeft(TimelineInfo timelineInfo) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		for (EventLink eventLink : timelineInfo.eventLinks()) {
			TextBuilder eventName = new TextBuilder(SUBTITLE);
			eventName.add(eventLink.name(), "event?id=" + eventLink.id());
			left.add(eventName);

			TextBuilder date = new TextBuilder(MISC);
			date.add(EventPageRenderer.formatDateString(eventLink.dateInfo()));
			left.add(date);

			TextBuilder eventDescription = new TextBuilder(NORMAL);
			eventDescription.add(eventLink.description());
			left.add(eventDescription);
		}
		return left;
	}

	private WidgetBuilder makeRight(TimelineInfo timelineInfo, PageBuilder pageBuilder, LazyLoadAccountNameScript lazyLoadAccountNameScript) {
		TableBuilder right = new TableBuilder(2, false);

		right.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Tags");}});
		right.add(new TimelineFilter("all-tags"));

		for (TagLink tagLink : timelineInfo.tagLinks()) {
			right.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color()));
			right.add(new TimelineFilter(tagLink.name()));
		}

		right.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Users");}});
		right.add(new TimelineFilter("all-users"));

		for (UserLink userLink : timelineInfo.userLinks()) {
			TextBuilder userNameText = new TextBuilder(NORMAL, "\n");
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + userLink.data().userData());
			lazyLoadAccountNameScript.add(userLink.data(), pageVariable);
			userNameText.add(pageVariable.toString(), "user?id=" + userLink.id());

			right.add(userNameText);
			right.add(new TimelineFilter(pageVariable.toString()));
		}

		return right;
	}
}
