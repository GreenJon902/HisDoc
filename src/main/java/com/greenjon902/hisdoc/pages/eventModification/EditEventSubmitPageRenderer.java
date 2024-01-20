package com.greenjon902.hisdoc.pages.eventModification;

import com.greenjon902.hisdoc.Permission;
import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.pages.HtmlPageRenderer;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;

public class EditEventSubmitPageRenderer extends HtmlPageRenderer {

	private final Dispatcher dispatcher;
	private final PermissionHandler permissionHandler;

	public EditEventSubmitPageRenderer(Dispatcher dispatcher, PermissionHandler permissionHandler) {
		this.dispatcher = dispatcher;
		this.permissionHandler = permissionHandler;
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		try {
			if (!permissionHandler.hasPermission(user.pid(), Permission.EDIT_EVENT)) {
				throw new IllegalStateException("You do not have permission, you should not be on this page!");
			}
			int updatedBy = user.pid();
			int eid = Integer.parseInt(user.post().get("eid"));
			String changelog = user.post().get("changelog");
			if (changelog == null) throw new RuntimeException("Got a null value for changelog");

			SubmittedEvent submittedEvent = SubmittedEvent.fromPost(user.post());
			dispatcher.updateEvent(eid, submittedEvent, updatedBy, changelog);

			return "<html><script>window.location.href = 'event?id=" + eid + "';</script><html>";


		} catch (Exception e) {
			throw new RuntimeException("An error occurred,\nquery=\n" + query + "\n\n", e);
		}
	}
}
