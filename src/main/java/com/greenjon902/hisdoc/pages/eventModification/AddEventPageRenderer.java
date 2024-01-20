package com.greenjon902.hisdoc.pages.eventModification;

import com.greenjon902.hisdoc.Permission;
import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.scripts.EnsureSessionIdScript;
import com.greenjon902.hisdoc.pageBuilder.scripts.UnloadMessageSenderScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.runners.papermc.PaperMcSessionHandlerImpl;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventInfo;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

/**
 * A page that can be used to add an event. This will check for verification of the user, to ensure they can add an
 * event, and that it can be registered under their {@link com.greenjon902.hisdoc.sql.results.PersonInfo person}.
 * Data will be uploaded with cookies using the {@link AddEventSubmitPageRenderer}.
 * On a successful submit, data will then be cleared from local storage.
 */
public class AddEventPageRenderer extends AbstractModifyEventPageRenderer {
	public AddEventPageRenderer(Dispatcher dispatcher, PermissionHandler permissionHandler, SessionHandler sessionHandler) {
		super(dispatcher, permissionHandler, sessionHandler);
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("Add Event");

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		if (permissionHandler.hasPermission(user.pid(), Permission.ADD_EVENT)) {
			UnloadMessageSenderScript unloadMessageSenderScript = new UnloadMessageSenderScript(
					"Are you sure you want to leave, you will loose all submitted event info!", "addEventForm");
			pageBuilder.addScript(unloadMessageSenderScript);

			renderValid(pageBuilder, user, null);

		} else {
			renderInvalid(pageBuilder, user);
		}

		return pageBuilder.render(user);
	}

	private void renderValid(PageBuilder pageBuilder, User user, EventInfo eventInfo) throws SQLException {
		pageBuilder.add(new TextBuilder(TITLE) {{add("Add Event");}});

		FormBuilder form = new FormBuilder("addEventForm", FormBuilder.Method.POST, "addEventSubmit");
		renderEventFormItems(form, eventInfo);

		form.add(new TextBuilder(SUBTITLE) {{add("Submit");}});
		form.add(new TextBuilder(NORMAL, "\n", null) {{
			add("This event will be submitted under the user " + getNameOf(user.pid()) + ".");
			//add("An administrator will then look over the event before making it public or contacting you over any modifications or clarifications.");
			// That message is planned to be added with event screening in v2
		}});
		form.add(new FormBuilder.SubmitButtonBuilder());

		pageBuilder.add(form);
	}

	private void renderInvalid(PageBuilder pageBuilder, User user) {
		pageBuilder.add(new TextBuilder(TITLE) {{add("You are not authorised to do that");}});
		pageBuilder.add(new TextBuilder(NORMAL) {{add("Have you linked your account to HisDoc? Try typing: ");}});
		pageBuilder.add(new TextBuilder(CODE) {{add(((PaperMcSessionHandlerImpl) sessionHandler).makeCommand(user.sessionId()));}});
		pageBuilder.add(new TextBuilder(NORMAL) {{add("And then reloading this page.");}});
		pageBuilder.add(new TextBuilder(NORMAL) {{add("If you have, and you think you should have permission, please contact your administrator!");}});
		pageBuilder.addScript(new EnsureSessionIdScript(pageBuilder));
	}
}
