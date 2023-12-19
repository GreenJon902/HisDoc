package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.NORMAL;
import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.TITLE;

public class HomePageRenderer extends HtmlPageRenderer {
	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("HisDoc");
		pageBuilder.add(new NavBarBuilder(pageBuilder));

		pageBuilder.add(new TextBuilder(TITLE) {{add("Welcome");}});
		pageBuilder.add(new TextBuilder(NORMAL) {{
			add("to HisDoc, the wonderful history documentation program for small communities. If your reading this, " +
					"your probably new here and thinking \"wait no but why?\". Well you see, I have run a minecraft " +
					"server for over two years now, but I don't want to forget all the amazing things we've done in " +
					"the past. Hence HisDoc.\n\n");
			add("The system used 3 main components, events, people, and tags. Events are as their name is, events that " +
					"have occurred in this community. People just mean someone that has participated or was related to " +
					"an event. And tags are just a way of grouping events.\n\n");
			add("So now you probably wondering what we can do, well first of we can construct a filterable timeline " +
					"of all events on the timeline page. We can also show statistics of specific people and tags, " +
					"as well as track extra information about events - for example specifying part of an event that needs " +
					"to be confirmed or clarified before it is correct!\n\n");
			add("This system was built by me (GreenJon902). If you need any help or find a bug or have a feature " +
					"suggestion then use the issue tracker on the ");
			add("github", "https://github.com/GreenJon902/HisDoc", true);
			add(". I mean you could also contribute if you really want to ");
			add(128521);
			add(".");
		}});

		return pageBuilder.render(user);
	}
}
