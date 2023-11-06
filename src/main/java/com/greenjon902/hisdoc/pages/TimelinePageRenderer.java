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
		column.add(makeRight(timelineInfo, pageBuilder, lazyLoadAccountNameScript, query));
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

	private WidgetBuilder makeRight(TimelineInfo timelineInfo, PageBuilder pageBuilder, LazyLoadAccountNameScript lazyLoadAccountNameScript, Map<String, String> query) {
		Form right = new Form("timeline-filters");
		TableBuilder table = new TableBuilder(2, false);

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Tags");}});
		table.add(new BreakBuilder());

		for (TagLink tagLink : timelineInfo.tagLinks()) {
			table.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color()));
			table.add(new TimelineFilter(tagLink.name(), query.get(tagLink.name())));
		}

		table.add(new BreakBuilder());
		table.add(new BreakBuilder());

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Users");}});
		table.add(new BreakBuilder());

		for (UserLink userLink : timelineInfo.userLinks()) {
			TextBuilder userNameText = new TextBuilder(NORMAL, "\n");
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + userLink.data().userData());
			lazyLoadAccountNameScript.add(userLink.data(), pageVariable);
			userNameText.add(pageVariable.toString(), "user?id=" + userLink.id());

			table.add(userNameText);
			table.add(new TimelineFilter(userLink.data().userData(), query.get(userLink.data().userData())));
		}

		right.add(table);
		right.add(new DateRangeSlider(timelineInfo.eventLinks().get(0).dateInfo().date1(),
				timelineInfo.eventLinks().get(timelineInfo.eventLinks().size() - 1).dateInfo().date1()));

		right.add(new BreakBuilder());
		right.add(new SubmitButton());

		return right;
	}
}
