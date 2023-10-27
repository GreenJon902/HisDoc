package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.*;
import com.greenjon902.hisdoc.webDriver.PageRenderer;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EventPageRenderer extends PageRenderer {

	private final Dispatcher dispatcher;

	public EventPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment) throws SQLException {  // TODO: Posted date and eid
		if (!query.containsKey("id")) {
			return "No id given :(";
		}

		int eventId;
		try {
			eventId = Integer.parseInt(query.get("id"));
		} catch (NumberFormatException e) {
			return "Malformed event id :(";
		}
		EventInfo eventInfo = dispatcher.getEventInfo(eventId);

		if (eventInfo == null) {
			return "No event found :(";
		}

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.add(new LogoBuilder());
		pageBuilder.add(new SeparatorBuilder(0.3));

		// -------------------
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		TitleBuilder titleBuilder = new TitleBuilder();
		titleBuilder.add(eventInfo.name());
		left.add(titleBuilder);

		TitleBuilder descriptionTitleBuilder = TitleBuilder.subtitleBuilder();
		descriptionTitleBuilder.add("Description");
		left.add(descriptionTitleBuilder);
		TextBuilder descriptionTextBuilder = new TextBuilder();
		descriptionTextBuilder.add(eventInfo.description());
		left.add(descriptionTextBuilder);

		TitleBuilder changelogTitleBuilder = TitleBuilder.subtitleBuilder();
		changelogTitleBuilder.add("Changelog");
		left.add(changelogTitleBuilder);
		TextBuilder changelogTextBuilder = new TextBuilder();
		for (ChangeInfo changeInfo : eventInfo.changeInfos()) {
			changelogTextBuilder.add(changeInfo.date() + " " + changeInfo.author() + ": ");
			changelogTextBuilder.add(changeInfo.description() + "\n", 0xaaa, 0);
		}
		left.add(changelogTextBuilder);
		// -------------------

		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		TitleBuilder tagTitles = TitleBuilder.auxiliaryInfoTitleBuilder();
		tagTitles.add("Tags");
		right.add(tagTitles);
		ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");
		for (TagLink tagLink : eventInfo.tagLinks()) {
			tagContainer.add(new TagBuilder(tagLink.name(), tagLink.id(), tagLink.color()));
		}
		right.add(tagContainer);
		right.add(new BreakBuilder());

		TitleBuilder relatedEventTitles = TitleBuilder.auxiliaryInfoTitleBuilder();
		relatedEventTitles.add("Related Events");
		right.add(relatedEventTitles);
		TextBuilder relatedEvents = new TextBuilder("\n");
		// TODO: Make these
		relatedEvents.add("a", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		relatedEvents.add("bcdefghijklmnopqrstuvplaw", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		relatedEvents.add("3w4aet gaerta eraerhyg a", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		relatedEvents.add("3w4aet gaerta eraerhyg awawer g", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
		right.add(relatedEvents);
		right.add(new BreakBuilder());

		TitleBuilder relatedUserTitles = TitleBuilder.auxiliaryInfoTitleBuilder();
		relatedUserTitles.add("Related Users");
		right.add(relatedUserTitles);
		TextBuilder relatedUsers = new TextBuilder("\n");
		for (UserLink userLink : eventInfo.relatedPlayerInfos()) {
			relatedUsers.add(userLink.userInfo(), "user?id:" + userLink.id());
		}
		right.add(relatedUsers);
		// -------------------


		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(left);
		columnLayoutBuilder.add(right);

		pageBuilder.add(columnLayoutBuilder);
		return pageBuilder.render();
	}

}
