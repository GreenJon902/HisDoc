package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.PersonLink;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.*;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.NORMAL;

public class PersonsPageRenderer extends HtmlPageRenderer {

	private final Dispatcher dispatcher;

	public PersonsPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		List<PersonLink> personLinks = new ArrayList<> (dispatcher.getAllPersonLinks());
		personLinks.sort(Comparator.comparing(o -> o.person().name()));

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("People");

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		if (personLinks.isEmpty()) {
			pageBuilder.add(new TextBuilder(NORMAL) {{
				add("No people exist"); }});
		} else {
			TextBuilder persons = new TextBuilder(NORMAL, "", "personContainer");
			for (PersonLink personLink : personLinks) {
				persons.add(personLink.person().name() + "\n", "person?id=" + personLink.id(), false);
			}
			pageBuilder.add(persons);
		}

		return pageBuilder.render(user);
	}
}
