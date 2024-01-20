package com.greenjon902.hisdoc.pages.eventModification;

import com.greenjon902.hisdoc.Permission;
import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.flexiDateTime.CenteredFlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.RangedFlexiDate;
import com.greenjon902.hisdoc.pages.HtmlPageRenderer;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AddEventSubmitPageRenderer extends HtmlPageRenderer {

	private final Dispatcher dispatcher;
	private final PermissionHandler permissionHandler;

	public AddEventSubmitPageRenderer(Dispatcher dispatcher, PermissionHandler permissionHandler) {
		this.dispatcher = dispatcher;
		this.permissionHandler = permissionHandler;
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		try {
			if (!permissionHandler.hasPermission(user.pid(), Permission.ADD_EVENT)) {
				throw new IllegalStateException("You do not have permission, you should not be on this page!");
			}
			int postedBy = user.pid();

			SubmittedEvent submittedEvent = SubmittedEvent.fromPost(user.post());
			int eid = dispatcher.addEvent(submittedEvent, postedBy);

			return "<html><body><p>" +
					"The event has been added, would you like to " +
					"<a href=\"event?id=" + eid + "\" target=\"_blank\">see it</a> or " +  // target="_blank" means open new tab
					"<a href=\"add\" target=\"_blank\">add a new event</a>" +
					"</p></body></html>";


		} catch (Exception e) {
			throw new RuntimeException("An error occurred,\nquery=\n" + query + "\n\n", e);
		}
	}
}
