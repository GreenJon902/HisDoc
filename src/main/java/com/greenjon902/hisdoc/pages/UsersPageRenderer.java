package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.UserLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.Session;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.NORMAL;

public class UsersPageRenderer extends PageRenderer {

	private final Dispatcher dispatcher;

	public UsersPageRenderer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(Map<String, String> query, String fragment, Session session) throws SQLException {
		Set<UserLink> userLinks = dispatcher.getAllUserLinks();

		PageBuilder pageBuilder = new PageBuilder();
		LazyLoadAccountNameScript lazyLoadAccountNameScript = new LazyLoadAccountNameScript();  // Variables added elsewhere
		pageBuilder.addScript(lazyLoadAccountNameScript);

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		TextBuilder users = new TextBuilder(NORMAL, "\n");
		for (UserLink userLink : userLinks) {
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + userLink.data().userData());
			lazyLoadAccountNameScript.add(userLink.data(), pageVariable);
			users.add(pageVariable.toString(), "user?id=" + userLink.id());
		}
		pageBuilder.add(users);

		return pageBuilder.render(session);
	}
}
