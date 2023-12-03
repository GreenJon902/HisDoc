package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.scripts.TimelineSearchFilterScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventLink;
import com.greenjon902.hisdoc.sql.results.PersonLink;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.sql.results.TimelineInfo;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

public class TimelinePageRenderer extends PageRenderer {

	private final Dispatcher dispatcher;

	public TimelinePageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		TimelineInfo timelineInfo = dispatcher.getTimelineInfo();

		if (timelineInfo == null) {
			return "No timeline info found?";
		}

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("Timeline");

		LazyLoadAccountNameScript lazyLoadAccountNameScript = new LazyLoadAccountNameScript();  // Variables added elsewhere
		pageBuilder.addScript(lazyLoadAccountNameScript);
		TimelineSearchFilterScript searchFilterScript = new TimelineSearchFilterScript(pageBuilder);  // Variables added elsewhere
		pageBuilder.addScript(searchFilterScript);

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		if (timelineInfo.eventLinks().isEmpty()) {
			pageBuilder.add(new TextBuilder(NORMAL) {{
				add("No events exist"); }});

		} else {
			pageBuilder.add(new TextBuilder(MAJOR_SUBTITLE) {{
				add("Filters");
			}});
			pageBuilder.add(makeTop(timelineInfo, pageBuilder, lazyLoadAccountNameScript, user, searchFilterScript));
			pageBuilder.add(new TextBuilder(MAJOR_SUBTITLE) {{
				add("Timeline");
			}});
			pageBuilder.add(makeBottom(timelineInfo, searchFilterScript));
		}

		return pageBuilder.render(user);
	}

	private ContainerWidgetBuilder makeBottom(TimelineInfo timelineInfo, TimelineSearchFilterScript searchFilterScript) {
		ContainerWidgetBuilder bottom = new ContainerWidgetBuilder();

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

			bottom.add(event);
			searchFilterScript.add(event, Stream.concat(
					timelineInfo.eventTagRelations().getOrDefault(eventLink, new ArrayList<>()).stream().map(TagLink::name),
					timelineInfo.eventPersonRelations().getOrDefault(eventLink, new ArrayList<>()).stream().map(person -> person.data().personData())
			), eventLink.dateInfo());
		}
		return bottom;
	}

	private WidgetBuilder makeTop(TimelineInfo timelineInfo, PageBuilder pageBuilder, LazyLoadAccountNameScript lazyLoadAccountNameScript, User user, TimelineSearchFilterScript searchFilterScript) {
		ContainerWidgetBuilder top = new ContainerWidgetBuilder();

		ContainerWidgetBuilder filterButtons = new ContainerWidgetBuilder("filter-buttons-holder");

		TableBuilder table = new TableBuilder(2, false);

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("Set All To");}});
		ContainerWidgetBuilder setAllContainer = new ContainerWidgetBuilder("timeline-filters-setall-holder");
		setAllContainer.add(new Button("Exclude", "setAllFilters('Exclude');filterChanged()"));
		setAllContainer.add(new Button("Ignore", "setAllFilters('Ignore');filterChanged()"));
		setAllContainer.add(new Button("Include", "setAllFilters('Include');filterChanged()"));
		table.add(setAllContainer);

		top.add(table);

		table = new TableBuilder(2, false);

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Tags");}});
		table.add(new BreakBuilder());

		for (TagLink tagLink : timelineInfo.tagLinks()) {
			table.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color()));
			TimelineFilter timelineFilter = new TimelineFilter(tagLink.name(),
					user.otherCookies().getOrDefault(tagLink.name(), "Include"), "filterChanged()");
			table.add(timelineFilter);
			searchFilterScript.add(timelineFilter);
		}

		filterButtons.add(table);

		table = new TableBuilder(2, false);

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("All Persons");}});
		table.add(new BreakBuilder());

		for (PersonLink personLink : timelineInfo.personLinks()) {
			TextBuilder personNameText = new TextBuilder(NORMAL, "\n");
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + personLink.data().personData());
			lazyLoadAccountNameScript.add(personLink.data(), pageVariable);
			personNameText.add(pageVariable.toString(), "person?id=" + personLink.id());

			table.add(personNameText);
			TimelineFilter timelineFilter = new TimelineFilter(personLink.data().personData(),
					user.otherCookies().getOrDefault(personLink.data().personData(), "Include"), "filterChanged()");
			table.add(timelineFilter);
			searchFilterScript.add(timelineFilter);
		}

		filterButtons.add(table);

		table = new TableBuilder(2, false);

		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("Date Range");}});
		table.add(new BreakBuilder());

		table.add(new TextBuilder(NORMAL) {{add("Start Date:");}});
		if (user.otherCookies().containsKey("date1") && !user.otherCookies().get("date1").isEmpty()) {
			table.add(new DateSelector(new Timestamp(Date.valueOf(user.otherCookies().get("date1")).getTime()), "date1", "filterChanged()"));
		} else {
			table.add(new DateSelector("date1", "filterChanged()"));
		}
		table.add(new TextBuilder(NORMAL) {{add("End Date:");}});
		if (user.otherCookies().containsKey("date2") && !user.otherCookies().get("date2").isEmpty()) {
			table.add(new DateSelector(new Timestamp(Date.valueOf(user.otherCookies().get("date2")).getTime()), "date2", "filterChanged()"));
		} else {
			table.add(new DateSelector("date2", "filterChanged()"));
		}
		table.add(new TextBuilder(NORMAL) {{add("Selection Method:");}});
		table.add(new RadioButton("dateSelectionMethod",
				user.otherCookies().getOrDefault("dateSelectionMethod", "Inclusive"),
				List.of("Exclusive", "Inclusive"), "filterChanged()"));

		table.add(new BreakBuilder());
		table.add(new BreakBuilder());
		table.add(new TextBuilder(AUX_INFO_TITLE) {{add("Search:");}});
		table.add(new BreakBuilder());
		table.add(new TextBuilder(NORMAL) {{add("Text: ");}});
		table.add(new FormBuilder.TextInputBuilder("filterText", 1, "", "filterChanged()", user.otherCookies().getOrDefault("filterText", "")));
		filterButtons.add(table);
		top.add(filterButtons);

		return top;
	}
}
