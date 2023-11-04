package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventLink;
import com.greenjon902.hisdoc.sql.results.TimelineInfo;
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
		pageBuilder.add(new NavBarBuilder(pageBuilder));


		for (EventLink eventLink : timelineInfo.eventLinks()) {
			TextBuilder eventName = new TextBuilder(SUBTITLE);
			eventName.add(eventLink.name(), "event?id=" + eventLink.id());
			pageBuilder.add(eventName);

			TextBuilder date = new TextBuilder(MISC);
			date.add(EventPageRenderer.formatDateString(eventLink.dateInfo()));
			pageBuilder.add(date);

			TextBuilder eventDescription = new TextBuilder(NORMAL);
			eventDescription.add(eventLink.description());
			pageBuilder.add(eventDescription);
		}


		return pageBuilder.render(session);
	}
}
