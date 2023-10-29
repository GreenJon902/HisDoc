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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

// TODO: Format account names from uuids into descriptions and stuffs

public class EventPageRenderer extends PageRenderer {

	private final Dispatcher dispatcher;

	public EventPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, Session session) throws SQLException {  // TODO: Posted date and eid
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

		ContainerWidgetBuilder left = makeLeft(eventInfo);
		ContainerWidgetBuilder right = makeRight(eventInfo, lazyLoadAccountNameScript, pageBuilder);


		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(left);
		columnLayoutBuilder.add(right);

		pageBuilder.add(columnLayoutBuilder);
		return pageBuilder.render(session);
	}

	private ContainerWidgetBuilder makeLeft(EventInfo eventInfo) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		TextBuilder titleBuilder = new TextBuilder(TITLE);
		titleBuilder.add(eventInfo.name());
		left.add(titleBuilder);

		TextBuilder idTextBuilder = new TextBuilder(MISC);
		idTextBuilder.add("EID: " + eventInfo.eid());
		left.add(idTextBuilder);

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
		// TODO: Make these
		relatedEvents.add("a", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		relatedEvents.add("bcdefghijklmnopqrstuvplaw", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		relatedEvents.add("3w4aet gaerta eraerhyg a", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		relatedEvents.add("3w4aet gaerta eraerhyg awawer g", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		right.add(relatedEvents);
		right.add(new BreakBuilder());

		TextBuilder relatedUserTitles = new TextBuilder(AUX_INFO_TITLE);
		relatedUserTitles.add("Related Users");
		right.add(relatedUserTitles);
		TextBuilder relatedUsers = new TextBuilder(NORMAL, "\n");
		for (UserLink userLink : eventInfo.relatedPlayerInfos()) {
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + userLink.userInfo());
			lazyLoadAccountNameScript.add(userLink.userInfo(), pageVariable);
			relatedUsers.add(pageVariable.toString(), "user?id=" + userLink.id());
		}
		right.add(relatedUsers);

		return right;
	}

	private WidgetBuilder makeChangelogContents(EventInfo eventInfo) {
		String postedBy = "Unknown";
		if (eventInfo.postedDate() != null) {
			postedBy = eventInfo.postedBy().userInfo();
		}

		int maxSpace = postedBy.length();
		for (ChangeInfo changeInfo : eventInfo.changeInfos()) {
			int space;
			if ((space = changeInfo.author().userInfo().length()) > maxSpace) {
				maxSpace = space;
			}
		}

		TextBuilder changelog = new TextBuilder(NORMAL);

		String postedDate = "Unknown";
		if (eventInfo.postedDate() != null) {
			postedDate = eventInfo.postedDate().toString();
		}
		changelog.add(postedDate + " " + String.format("%1$" + maxSpace + "s", postedBy) + ": ");
		changelog.add("This event was created!\n", 0xaaaaaa, 0);

		for (ChangeInfo changeInfo : eventInfo.changeInfos()) {
			changelog.add(changeInfo.date() + " " + String.format("%1$" + maxSpace + "s", changeInfo.author()) + ": ");
			changelog.add(changeInfo.description() + "\n", 0xaaaaaa, 0);
		}

		return changelog;
	}


	public static String formatDateString(DateInfo dateInfo) {
		if (Objects.equals(dateInfo.type(), "c")) {
			String pattern = "";
			switch (dateInfo.precision()) {
				case "d": pattern += "yyyy-MM-dd";
				case "h": pattern += " hh";
				case "m": pattern += ":mm";
			}
			String center = dateInfo.date1().toLocalDateTime().format(DateTimeFormatter.ofPattern(pattern));
			String diff = "";
			if (dateInfo.diff() != 0) {
				diff = " &plusmn;" + String.format("%02d", dateInfo.diff()) + dateInfo.diffType().toUpperCase(Locale.ROOT);
			}
			return center + diff;
		} else {
			return "Somewhere between " + dateInfo.date1().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
					" and " + dateInfo.date2().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
	}
}
