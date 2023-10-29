package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventLink;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.sql.results.UserInfo;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.Session;
import io.quickchart.QuickChart;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

// TODO: User info needs to be a UUID, which needs to then convert itself to a username on the client
// TODO: User page should be specific to user info prefix

public class UserPageRenderer extends PageRenderer {
	private final Dispatcher dispatcher;

	public UserPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public String render(Map<String, String> query, String fragment, Session session) throws SQLException {
		if (!query.containsKey("id")) {
			return "No id given :(";
		}

		int userId;
		try {
			userId = Integer.parseInt(query.get("id"));
		} catch (NumberFormatException e) {
			return "Malformed user id :(\nUID: " + query.get("id");
		}
		UserInfo userInfo = dispatcher.getUserInfo(userId);

		if (userInfo == null) {
			return "No user found :(\nTID: " + userId + " (" + query.get("id") + ")";
		}

		PageBuilder pageBuilder = new PageBuilder();
		PageVariable accountNameVar = pageBuilder.addVariable("account-name");
		pageBuilder.addScript(new LazyLoadAccountNameScript(userInfo.userInfo(), accountNameVar));

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ContainerWidgetBuilder left = makeLeft(userInfo, accountNameVar);
		ContainerWidgetBuilder right = makeRight(userInfo, accountNameVar);

		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(left);
		columnLayoutBuilder.add(right);

		pageBuilder.add(columnLayoutBuilder);
		return pageBuilder.render(session);
	}

	private ContainerWidgetBuilder makeLeft(UserInfo userInfo, PageVariable accountNameVar) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		TextBuilder titleBuilder = new TextBuilder(TITLE);
		titleBuilder.add(accountNameVar);
		left.add(titleBuilder);

		TextBuilder idTextBuilder = new TextBuilder(MISC);
		idTextBuilder.add("UID: " + userInfo.uid());
		left.add(idTextBuilder);

		TextBuilder recentEventsTitle = new TextBuilder(SUBTITLE);
		recentEventsTitle.add("Recent Events");
		left.add(recentEventsTitle);
		WidgetBuilder recentEvents = makeRecentEventContents(userInfo);
		left.add(recentEvents);

		TextBuilder tagTitle = new TextBuilder(SUBTITLE);
		tagTitle.add("Tags");
		left.add(tagTitle);
		WidgetBuilder tag = makeTagStatsContents(userInfo);
		left.add(tag);

		return left;
	}

	private WidgetBuilder makeRecentEventContents(UserInfo userInfo) {
		TextBuilder recentEvents = new TextBuilder(NORMAL);

		for (EventLink eventLink : userInfo.recentEventLinks()) {
			recentEvents.add(EventPageRenderer.formatDateString(eventLink.dateInfo()));
			recentEvents.add(" - ");
			recentEvents.add(eventLink.name(), "event?id=" + eventLink.id());
			recentEvents.add("\n");
		}

		return recentEvents;
	}

	private WidgetBuilder makeTagStatsContents(UserInfo userInfo) {
		StringBuilder labels = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder colors = new StringBuilder();
		for (Map.Entry<TagLink, Integer> entry : userInfo.countedTagLinks().entrySet()) {
			labels.append("'");
			labels.append(entry.getKey().name()); // TODO: Find some way to link to tag page
			labels.append("', ");
			values.append(entry.getValue());
			values.append(", ");
			colors.append("'");
			colors.append("#").append(String.format("%06x", entry.getKey().color()));
			colors.append("', ");
		}

		QuickChart chart = new QuickChart();
		chart.setConfig("{type: 'bar'," +
						"data: {labels: [" + labels + "]," +
										 "datasets: [{ label: 'Users', " +
													   "data: [" + values + "], " +
													   "backgroundColor: [" + colors + "] },],}," +
						"options: {title: {display: false}, legend: {display: false}," +
									"scales: {yAxes: [{" +
											"id: 'Y1', " +
											"ticks: {stepSize: 1}" +
											"}]}}}");
		String url = chart.getUrl();
		return new ImageBuilder(url);
	}

	private ContainerWidgetBuilder makeRight(UserInfo userInfo, PageVariable accountNameVar) {
		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		IframeBuilder iframeBuilder = new IframeBuilder("https://minerender.org/embed/skin/?skin=" + accountNameVar);
		right.add(iframeBuilder);

		TextBuilder miscInfo = new TextBuilder(MISC, "\n");
		miscInfo.add("See on NameMC", "https://namemc.com/profile/" + userInfo.userInfo());
		miscInfo.add("Info: " + userInfo.userInfo());
		miscInfo.add("Post Count: " + userInfo.postCount());
		miscInfo.add("Event Count: " + userInfo.eventCount());
		right.add(miscInfo);

		return right;
	}
}
