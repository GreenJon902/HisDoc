package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.*;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.Session;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

// TODO: Format account names from uuids into descriptions and stuffs

public class EventPageRenderer extends PageRenderer {

	private final Dispatcher dispatcher;

	public EventPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, Session session) throws SQLException {
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
		LazyLoadAccountNameScript lazyLoadAccountNameScript = new LazyLoadAccountNameScript();  // Variables added elsewhere
		pageBuilder.addScript(lazyLoadAccountNameScript);

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ContainerWidgetBuilder left = makeLeft(eventInfo, lazyLoadAccountNameScript, pageBuilder);
		ContainerWidgetBuilder right = makeRight(eventInfo, lazyLoadAccountNameScript, pageBuilder);


		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(left);
		columnLayoutBuilder.add(right);

		pageBuilder.add(columnLayoutBuilder);
		return pageBuilder.render(session);
	}

	private ContainerWidgetBuilder makeLeft(EventInfo eventInfo, LazyLoadAccountNameScript lazyLoadAccountNameScript, PageBuilder pageBuilder) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		TextBuilder titleBuilder = new TextBuilder(TITLE);
		titleBuilder.add(eventInfo.name());
		left.add(titleBuilder);

		TextBuilder idTextBuilder = new TextBuilder(MISC);
		idTextBuilder.add("EID: " + eventInfo.eid());
		left.add(idTextBuilder);
		System.out.println(eventInfo.details());
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
		WidgetBuilder changelogTextBuilder = makeChangelogContents(eventInfo, lazyLoadAccountNameScript, pageBuilder);
		left.add(changelogTextBuilder);

		return left;
	}

	private ContainerWidgetBuilder makeRight(EventInfo eventInfo, LazyLoadAccountNameScript lazyLoadAccountNameScript, PageBuilder pageBuilder) {
		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		TextBuilder date = new TextBuilder(MISC);
		date.add(formatDateString(eventInfo.eventDateInfo()));
		right.add(date);

		right.add(new BreakBuilder());

		TextBuilder tagTitles = new TextBuilder(AUX_INFO_TITLE);
		tagTitles.add("Tags");
		right.add(tagTitles);
		ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");
		for (TagLink tagLink : eventInfo.tagLinks()) {
			tagContainer.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color()));
		}
		right.add(tagContainer);
		right.add(new BreakBuilder());

		TextBuilder relatedEventTitles = new TextBuilder(AUX_INFO_TITLE);
		relatedEventTitles.add("Related Events");
		right.add(relatedEventTitles);
		TextBuilder relatedEvents = new TextBuilder(NORMAL, "\n");
		for (EventLink eventLink : eventInfo.relatedEventLinks()) {
			relatedEvents.add(eventLink.name(), "event?id=" + eventLink.id());
		}
		right.add(relatedEvents);
		right.add(new BreakBuilder());

		TextBuilder relatedUserTitles = new TextBuilder(AUX_INFO_TITLE);
		relatedUserTitles.add("Related Users");
		right.add(relatedUserTitles);
		TextBuilder relatedUsers = new TextBuilder(NORMAL, "\n");
		for (UserLink userLink : eventInfo.relatedPlayerInfos()) {
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + userLink.data().userData());
			lazyLoadAccountNameScript.add(userLink.data(), pageVariable);
			relatedUsers.add(pageVariable.toString(), "user?id=" + userLink.id());
		}
		right.add(relatedUsers);

		return right;
	}

	private WidgetBuilder makeChangelogContents(EventInfo eventInfo, LazyLoadAccountNameScript lazyLoadAccountNameScript, PageBuilder pageBuilder) {
		// Contents means not title, it does not mean only create table contents. (so we will use the <table> tag)

		TableBuilder table = new TableBuilder(3, true);  // Date, Author, Description

		// First we add the event author
		if (eventInfo.postedDate() == null) table.add(new TextBuilder(NORMAL) {{ add("Unknown"); }});
		else table.add(new TextBuilder(NORMAL) {{ add(eventInfo.postedDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"))); }});

		if (eventInfo.postedBy() == null) table.add(new TextBuilder(NORMAL) {{ add("Unknown:"); }});
		else {
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + eventInfo.postedBy().data().userData());
			lazyLoadAccountNameScript.add(eventInfo.postedBy().data(), pageVariable);
			table.add(new TextBuilder(NORMAL) {{ add(pageVariable + ":"); }});
		}
		table.add(new TextBuilder(MISC) {{ add("This event was created!"); }});

		// Then we add any changes
		for (ChangeInfo changeInfo : eventInfo.changeInfos()) {
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + changeInfo.author().data().userData());
			lazyLoadAccountNameScript.add(changeInfo.author().data(), pageVariable);

			table.add(new TextBuilder(NORMAL) {{ add(changeInfo.date().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"))); }});
			table.add(new TextBuilder(NORMAL) {{ add(pageVariable + ":"); }});
			table.add(new TextBuilder(MISC) {{ add(changeInfo.description()); }});
		}

		return table;
	}


	public static String formatDateString(DateInfo dateInfo) {
		if (dateInfo.type() == DateInfo.Type.CENTERED) {
			String pattern =
			switch (dateInfo.precision()) {
				case MINUTE -> "yyyy-MM-dd hh:mm";
				case HOUR -> "yyyy-MM-dd hh:??";
				case DAY -> "yyyy-MM-dd";
			};
			String center = dateInfo.date1().toLocalDateTime().format(DateTimeFormatter.ofPattern(pattern));
			String diff = "";
			if (dateInfo.diff() != 0) {
				diff = " ±" + dateInfo.diff() + dateInfo.diffType().toString().charAt(0);
			}
			return center + diff;
		} else {
			return "Somewhere between " + dateInfo.date1().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
					" and " + dateInfo.date2().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
	}
}
