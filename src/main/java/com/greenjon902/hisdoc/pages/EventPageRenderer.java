package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.*;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

public class EventPageRenderer extends HtmlPageRenderer {

	private final Dispatcher dispatcher;

	public EventPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		if (!query.containsKey("id")) {
			return "No id given :(";
		}

		int eventId;
		try {
			eventId = Integer.parseInt(query.get("id"));
		} catch (NumberFormatException e) {
			return "Malformed event id :(\nEID: " + query.get("id");
		}
		EventInfo eventInfo = dispatcher.getEventInfo(eventId);

		if (eventInfo == null) {
			return "No event found :(\nEID: " + eventId + " (" + query.get("id") + ")";
		}

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title(eventInfo.name());

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ContainerWidgetBuilder left = makeLeft(eventInfo);
		ContainerWidgetBuilder right = makeRight(eventInfo);


		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(left);
		columnLayoutBuilder.add(right);

		pageBuilder.add(columnLayoutBuilder);
		return pageBuilder.render(user);
	}

	private ContainerWidgetBuilder makeLeft(EventInfo eventInfo) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		TextBuilder titleBuilder = new TextBuilder(TITLE);
		titleBuilder.add(eventInfo.name());
		left.add(titleBuilder);

		TextBuilder idTextBuilder = new TextBuilder(MISC);
		idTextBuilder.add("EID: " + eventInfo.id());
		left.add(idTextBuilder);
		if (eventInfo.details() != null) {
			TextBuilder detailsBox = new TextBuilder(WARNING);
			detailsBox.add(eventInfo.details());
			left.add(detailsBox);
		}

		TextBuilder descriptionTitleBuilder = new TextBuilder(SUBTITLE);
		descriptionTitleBuilder.add("Description");
		left.add(descriptionTitleBuilder);
		TextBuilder descriptionTextBuilder = new TextBuilder(NORMAL);
		descriptionTextBuilder.add(eventInfo.description());
		left.add(descriptionTextBuilder);

		TextBuilder changelogTitleBuilder = new TextBuilder(SUBTITLE);
		changelogTitleBuilder.add("Changelog");
		left.add(changelogTitleBuilder);
		WidgetBuilder changelogTextBuilder = makeChangelogContents(eventInfo);
		left.add(changelogTextBuilder);

		return left;
	}

	private ContainerWidgetBuilder makeRight(EventInfo eventInfo) {
		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		TextBuilder date = new TextBuilder(MISC);
		date.add(eventInfo.eventDateInfo().formatString());
		right.add(date);

		right.add(new BreakBuilder());

		TextBuilder tagTitles = new TextBuilder(AUX_INFO_TITLE);
		tagTitles.add("Tags");
		right.add(tagTitles);
		ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");

		ArrayList<TagLink> tagLinks = new ArrayList<>(eventInfo.tagLinks());  // List so we can sort them
		tagLinks.sort(Comparator.comparing(TagLink::name));
		for (TagLink tagLink : tagLinks) {
			tagContainer.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color(), tagLink.description()));
		}
		right.add(tagContainer);
		right.add(new BreakBuilder());

		TextBuilder relatedEventTitles = new TextBuilder(AUX_INFO_TITLE);
		relatedEventTitles.add("Related Events");
		right.add(relatedEventTitles);
		TextBuilder relatedEvents = new TextBuilder(NORMAL, "\n", null);
		for (EventLink eventLink : eventInfo.relatedEventLinks()) {
			relatedEvents.add(eventLink.name(), "event?id=" + eventLink.id(), false);
		}
		right.add(relatedEvents);
		right.add(new BreakBuilder());

		TextBuilder relatedPersonTitles = new TextBuilder(AUX_INFO_TITLE);
		relatedPersonTitles.add("Related Persons");
		right.add(relatedPersonTitles);
		TextBuilder relatedPersons = new TextBuilder(NORMAL, "\n", null);

		List<PersonLink> personLinks = new ArrayList<>(eventInfo.relatedPersonLinks());
		personLinks.sort(Comparator.comparing(o -> o.person().name()));
		for (PersonLink personLink : personLinks) {
			relatedPersons.add(personLink.person().name(), "person?id=" + personLink.id(), false);
		}
		right.add(relatedPersons);

		return right;
	}

	private WidgetBuilder makeChangelogContents(EventInfo eventInfo) {
		// Contents means not title, it does not mean only create table contents. (so we will use the <table> tag)

		TableBuilder table = new TableBuilder(3, true);  // Date, Author, Description

		// First we add the event author
		if (eventInfo.postedDate() == null) table.add(new TextBuilder(NORMAL) {{ add("Unknown"); }});
		else table.add(new TextBuilder(NORMAL) {{ add(eventInfo.postedDate().formatString()); }});

		if (eventInfo.postedBy() == null) table.add(new TextBuilder(NORMAL) {{ add("Unknown:"); }});
		else {
			table.add(new TextBuilder(NORMAL) {{ add(eventInfo.postedBy().person().name(), "person?id=" + eventInfo.postedBy().id(), false); add(":"); }});
		}
		table.add(new TextBuilder(MISC) {{ add("This event was created!"); }});

		// Then we add any changes
		for (ChangeInfo changeInfo : eventInfo.changeInfos()) {
			if (changeInfo.date() == null) table.add(new TextBuilder(NORMAL) {{ add("Unknown"); }});
			else table.add(new TextBuilder(NORMAL) {{ add(changeInfo.date().formatString()); }});


			table.add(new TextBuilder(NORMAL) {{ add(changeInfo.author().person().name(), "person?id=" + changeInfo.author().id(), false); add(":"); }});
			table.add(new TextBuilder(MISC) {{ add(changeInfo.description()); }});
		}

		return table;
	}

	public static WidgetBuilder makeRecentEventContents(List<EventLink> recentEvents) {
		// Contents means not title, it does not mean only create table contents. (so we will use the <table> tag)

		if (recentEvents.isEmpty()) {
			return new TextBuilder(NORMAL) {{
				add("No events exist"); }};
		}

		TableBuilder table = new TableBuilder(2, false);  // Date, Event

		for (EventLink eventLink : recentEvents) {
			table.add(new TextBuilder(NORMAL) {{ add(eventLink.dateInfo().formatString() + " -"); }});
			table.add(new TextBuilder(NORMAL) {{ add(eventLink.name(), "event?id=" + eventLink.id(), false); }});
		}

		return table;
	}
}
