package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.person.MinecraftPerson;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.PersonInfo;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.webDriver.User;
import io.quickchart.QuickChart;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

public class PersonPageRenderer extends HtmlPageRenderer {
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

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		ContainerWidgetBuilder left = makeLeft(personInfo);
		ContainerWidgetBuilder right = makeRight(personInfo);

		ColumnLayoutBuilder columnLayoutBuilder = new ColumnLayoutBuilder();
		columnLayoutBuilder.add(left);
		columnLayoutBuilder.add(right);

		pageBuilder.add(columnLayoutBuilder);
		return pageBuilder.render(user);
	}

	private ContainerWidgetBuilder makeLeft(PersonInfo personInfo) {
		ContainerWidgetBuilder left = new ContainerWidgetBuilder();

		TextBuilder titleBuilder = new TextBuilder(TITLE);
		titleBuilder.add(personInfo.person().name());
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

		TextBuilder attributionText = new TextBuilder(MISC);
		attributionText.add("Render from ", 0b010);
		attributionText.add("QuickChart", "https://quickchart.io", false);
		left.add(attributionText);

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
											"ticks: {stepSize: 1, beginAtZero: true}" +
											"}]}}}");
		String url = chart.getUrl();
		return new ImageBuilder(url);
	}

	private ContainerWidgetBuilder makeRight(PersonInfo personInfo) {
		return switch (personInfo.person().type()) {
			case MINECRAFT -> makeMcRight(personInfo);
			case MISCELLANEOUS -> makeMiscRight(personInfo);
		};
	}

	private ContainerWidgetBuilder makeMcRight(PersonInfo personInfo) {
		MinecraftPerson person = (MinecraftPerson) personInfo.person();

		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		IframeBuilder iframeBuilder = new IframeBuilder("https://minerender.org/embed/skin/?skin=" + person.name());
		right.add(iframeBuilder);

		TextBuilder attributionText = new TextBuilder(MISC);
		attributionText.add("Render from ", 0b010);
		attributionText.add("Crafatar", "https://crafatar.com", false);
		right.add(attributionText);

		right.add(new BreakBuilder());

		TextBuilder miscInfo = new TextBuilder(MISC, "\n", null);
		miscInfo.add("See on NameMC", "https://namemc.com/profile/" + person.uuid(), false);
		miscInfo.add("UUID: " + person.uuid());
		miscInfo.add("Post Count: " + personInfo.postCount());
		miscInfo.add("Event Count: " + personInfo.eventCount());
		miscInfo.add("Ticks: " + person.ticks());
		right.add(miscInfo);

		return right;
	}

	private ContainerWidgetBuilder makeMiscRight(PersonInfo personInfo) {
		ContainerWidgetBuilder right = new ContainerWidgetBuilder();

		TextBuilder miscInfo = new TextBuilder(MISC, "\n", null);
		miscInfo.add("Post Count: " + personInfo.postCount());
		miscInfo.add("Event Count: " + personInfo.eventCount());
		right.add(miscInfo);

		return right;
	}
}
