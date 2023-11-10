package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.scripts.TimelineSearchFilterScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventLink;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.sql.results.TimelineInfo;
import com.greenjon902.hisdoc.sql.results.UserLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

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
		TimelineSearchFilterScript searchFilterScript = new TimelineSearchFilterScript(pageBuilder);  // Variables added elsewhere
		pageBuilder.addScript(searchFilterScript);

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ColumnLayoutBuilder column = new ColumnLayoutBuilder();
		column.add(makeLeft(timelineInfo, searchFilterScript));
		column.add(makeRight(timelineInfo, pageBuilder, lazyLoadAccountNameScript, session, searchFilterScript));
		pageBuilder.add(column);


		return pageBuilder.render(session);
	}

	private ContainerWidgetBuilder makeLeft(TimelineInfo timelineInfo, TimelineSearchFilterScript searchFilterScript) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		for (EventLink eventLink : timelineInfo.eventLinks()) {
			FilterableEvent event = new FilterableEvent(eventLink.name(), false);

			TextBuilder eventName = new TextBuilder(SUBTITLE);
			eventName.add(eventLink.name(), "event?id=" + eventLink.id());
			event.add(eventName);

			TextBuilder date = new TextBuilder(MISC);
			date.add(EventPageRenderer.formatDateString(eventLink.dateInfo()));
			event.add(date);

			TextBuilder eventDescription = new TextBuilder(NORMAL);
			eventDescription.add(eventLink.description());
			event.add(eventDescription);

			left.add(event);
			searchFilterScript.add(event, Stream.concat(
					timelineInfo.eventTagRelations().getOrDefault(eventLink, new ArrayList<>()).stream().map(TagLink::name),
					timelineInfo.eventUserRelations().getOrDefault(eventLink, new ArrayList<>()).stream().map(user -> user.data().userData())
			));
		}
		return left;
	}

	private WidgetBuilder makeRight(TimelineInfo timelineInfo, PageBuilder pageBuilder, LazyLoadAccountNameScript lazyLoadAccountNameScript, Session session, TimelineSearchFilterScript searchFilterScript) {
		ContainerWidgetBuilder right = new ContainerWidgetBuilder("timeline-filters");
		TableBuilder table = new TableBuilder(2, false);

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("Set All To");}});
		ContainerWidgetBuilder setAllContainer = new ContainerWidgetBuilder("timeline-filters-setall-holder");
		setAllContainer.add(new Button("Exclude", "setAllFilters('Exclude');filterChanged()"));
		setAllContainer.add(new Button("Ignore", "setAllFilters('Ignore');filterChanged()"));
		setAllContainer.add(new Button("Include", "setAllFilters('Include');filterChanged()"));
		table.add(setAllContainer);

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Tags");}});
		table.add(new BreakBuilder());

		for (TagLink tagLink : timelineInfo.tagLinks()) {
			table.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color()));
			TimelineFilter timelineFilter = new TimelineFilter(tagLink.name(), session.otherCookies().get(tagLink.name()));
			table.add(timelineFilter);
			searchFilterScript.add(timelineFilter);
		}

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Users");}});
		table.add(new BreakBuilder());

		for (UserLink userLink : timelineInfo.userLinks()) {
			TextBuilder userNameText = new TextBuilder(NORMAL, "\n");
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + userLink.data().userData());
			lazyLoadAccountNameScript.add(userLink.data(), pageVariable);
			userNameText.add(pageVariable.toString(), "user?id=" + userLink.id());

			table.add(userNameText);
			TimelineFilter timelineFilter = new TimelineFilter(userLink.data().userData(), session.otherCookies().get(userLink.data().userData()));
			table.add(timelineFilter);
			searchFilterScript.add(timelineFilter);
		}

		right.add(table);
		right.add(new DateRangeSlider(timelineInfo.eventLinks().get(0).dateInfo().date1(),
				timelineInfo.eventLinks().get(timelineInfo.eventLinks().size() - 1).dateInfo().date1()));

		return right;
	}
}
