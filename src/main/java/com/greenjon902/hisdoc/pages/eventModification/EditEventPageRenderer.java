package com.greenjon902.hisdoc.pages.eventModification;

import com.greenjon902.hisdoc.Permission;
import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.scripts.EnsureSessionIdScript;
import com.greenjon902.hisdoc.pageBuilder.scripts.UnloadMessageSenderScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.FormBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.NavBarBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.TextBuilder;
import com.greenjon902.hisdoc.runners.papermc.PaperMcSessionHandlerImpl;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventInfo;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;
import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.NORMAL;

public class EditEventPageRenderer extends AbstractModifyEventPageRenderer {
	public EditEventPageRenderer(Dispatcher dispatcher, PermissionHandler permissionHandler, SessionHandler sessionHandler) {
		super(dispatcher, permissionHandler, sessionHandler);
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
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
		pageBuilder.title("Modify Event");

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		if (permissionHandler.hasPermission(user.pid(), Permission.EDIT_EVENT)) {
			UnloadMessageSenderScript unloadMessageSenderScript = new UnloadMessageSenderScript(
					"Are you sure you want to leave, you will loose all edited event info!", "editEventForm");
			pageBuilder.addScript(unloadMessageSenderScript);

			renderValid(pageBuilder, user, eventInfo);

		} else {
			renderInvalid(pageBuilder, user);
		}

		return pageBuilder.render(user);
	}

	private void renderValid(PageBuilder pageBuilder, User user, EventInfo eventInfo) throws SQLException {
		pageBuilder.add(new TextBuilder(TITLE) {{add("Edit Event");}});

		FormBuilder form = new FormBuilder("editEventForm", FormBuilder.Method.POST, "editEventSubmit");
		form.add(new FormBuilder.HiddenInputBuilder("eid", String.valueOf(eventInfo.id())));


		renderEventFormItems(form, eventInfo);


		form.add(new TextBuilder(SUBTITLE) {{add("Change Description");}});
		form.add(new TextBuilder(NORMAL) {{add("Please enter a short summary of what you changed.");}});
		form.add(new FormBuilder.TextInputBuilder("changelog", 1, "", "", true));  // Pattern should be a not null block

		form.add(new TextBuilder(SUBTITLE) {{add("Submit");}});

		form.add(new TextBuilder(NORMAL, "\n", null) {{
			add("The change will be logged under the user " + getNameOf(user.pid()) + ".");
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
