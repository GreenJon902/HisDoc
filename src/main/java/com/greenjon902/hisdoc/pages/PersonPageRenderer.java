package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.PersonInfo;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;
import io.quickchart.QuickChart;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

public class PersonPageRenderer extends PageRenderer {
	private final Dispatcher dispatcher;

	public PersonPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		if (!query.containsKey("id")) {
			return "No id given :(";
		}

		int personId;
		try {
			personId = Integer.parseInt(query.get("id"));
		} catch (NumberFormatException e) {
			return "Malformed person id :(\nPID: " + query.get("id");
		}
		PersonInfo personInfo = dispatcher.getPersonInfo(personId);

		if (personInfo == null) {
			return "No person found :(\nTID: " + personId + " (" + query.get("id") + ")";
		}

		PageBuilder pageBuilder = new PageBuilder();
		PageVariable accountNameVar = pageBuilder.addVariable("account-name");
		pageBuilder.addScript(new LazyLoadAccountNameScript(personInfo.data(), accountNameVar));

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ContainerWidgetBuilder left = makeLeft(personInfo, accountNameVar);
		ContainerWidgetBuilder right = makeRight(personInfo, accountNameVar);

		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(left);
		columnLayoutBuilder.add(right);

		pageBuilder.add(columnLayoutBuilder);
		return pageBuilder.render(user);
	}

	private ContainerWidgetBuilder makeLeft(PersonInfo personInfo, PageVariable accountNameVar) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		TextBuilder titleBuilder = new TextBuilder(TITLE);
		titleBuilder.add(accountNameVar);
		left.add(titleBuilder);

		TextBuilder idTextBuilder = new TextBuilder(MISC);
		idTextBuilder.add("PID: " + personInfo.id());
		left.add(idTextBuilder);

		TextBuilder recentEventsTitle = new TextBuilder(SUBTITLE);
		recentEventsTitle.add("Recent Events");
		left.add(recentEventsTitle);
		WidgetBuilder recentEvents = EventPageRenderer.makeRecentEventContents(personInfo.recentEvents());
		left.add(recentEvents);

		TextBuilder recentPostsTitle = new TextBuilder(SUBTITLE);
		recentPostsTitle.add("Recent Posts");
		left.add(recentPostsTitle);
		WidgetBuilder recentPosts = EventPageRenderer.makeRecentEventContents(personInfo.recentPosts());
		left.add(recentPosts);

		TextBuilder tagTitle = new TextBuilder(SUBTITLE);
		tagTitle.add("Tags");
		left.add(tagTitle);
		WidgetBuilder tag = makeTagStatsContents(personInfo);
		left.add(tag);

		return left;
	}

	private WidgetBuilder makeTagStatsContents(PersonInfo personInfo) {
		StringBuilder labels = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder colors = new StringBuilder();
		for (Map.Entry<TagLink, Integer> entry : personInfo.countedTagLinks().entrySet()) {
			labels.append("'");
			labels.append(entry.getKey().name());
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
										 "datasets: [{ label: 'Persons', " +
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

	private ContainerWidgetBuilder makeRight(PersonInfo personInfo, PageVariable accountNameVar) {
		return switch (personInfo.data().type()) {
			case MINECRAFT -> makeMcRight(personInfo, accountNameVar);
			case MISCELLANEOUS -> makeMiscRight(personInfo);
		};
	}

	private ContainerWidgetBuilder makeMcRight(PersonInfo personInfo, PageVariable accountNameVar) {
		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		IframeBuilder iframeBuilder = new IframeBuilder("https://minerender.org/embed/skin/?skin=" + accountNameVar);
		right.add(iframeBuilder);

		TextBuilder miscInfo = new TextBuilder(MISC, "\n");
		miscInfo.add("See on NameMC", "https://namemc.com/profile/" + personInfo.data().personData());
		miscInfo.add("UPID: " + personInfo.data().personData());
		miscInfo.add("Post Count: " + personInfo.postCount());
		miscInfo.add("Event Count: " + personInfo.eventCount());
		right.add(miscInfo);

		return right;
	}

	private ContainerWidgetBuilder makeMiscRight(PersonInfo personInfo) {
		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		TextBuilder miscInfo = new TextBuilder(MISC, "\n");
		miscInfo.add("Post Count: " + personInfo.postCount());
		miscInfo.add("Event Count: " + personInfo.eventCount());
		right.add(miscInfo);

		return right;
	}
}