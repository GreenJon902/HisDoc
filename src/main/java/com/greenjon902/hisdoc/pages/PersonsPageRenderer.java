package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.PersonLink;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.NORMAL;

public class PersonsPageRenderer extends HtmlPageRenderer {

	private final Dispatcher dispatcher;

	public PersonsPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		Set<PersonLink> personLinks = dispatcher.getAllPersonLinks();

		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("People");

		LazyLoadAccountNameScript lazyLoadAccountNameScript = new LazyLoadAccountNameScript();  // Variables added elsewhere
		pageBuilder.addScript(lazyLoadAccountNameScript);

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		TextBuilder persons = new TextBuilder(NORMAL, "\n");
		for (PersonLink personLink : personLinks) {
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + personLink.data().personData());
			lazyLoadAccountNameScript.add(personLink.data(), pageVariable);
			persons.add(pageVariable.toString(), "person?id=" + personLink.id(), false);
		}
		pageBuilder.add(persons);

		return pageBuilder.render(user);
	}
}
