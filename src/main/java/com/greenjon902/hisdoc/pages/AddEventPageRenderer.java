package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.scripts.UnloadMessageSenderScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.PersonLink;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

/**
 * A page that can be used to add an event. This will check for verification of the user, to ensure they can add an
 * event, and that it can be registered under their {@link com.greenjon902.hisdoc.sql.results.PersonInfo person}.
 * Data will be uploaded with cookies using the {@link AddEventSubmitPageRenderer}.
 * On a successful submit, data will then be cleared from local storage.
 */
public class AddEventPageRenderer extends HtmlPageRenderer {
	private final Dispatcher dispatcher;
	private final SessionHandler sessionHandler;
	private final boolean feedforwardQuery; // Should the query info be given to the action page of the form too>

	public AddEventPageRenderer(Dispatcher dispatcher, SessionHandler sessionHandler, boolean feedforwardQuery) {
		this.dispatcher = dispatcher;
		this.sessionHandler = sessionHandler;
		this.feedforwardQuery = feedforwardQuery;
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("Add Event");

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		switch (sessionHandler.verify(user, query)) {
			case NO_SESSION -> renderNoSession(pageBuilder);
			case INVALID_IP -> renderInvalidIp(pageBuilder, user);
			case VALID -> {
				LazyLoadAccountNameScript lazyLoadAccountNameScript = new LazyLoadAccountNameScript();
				pageBuilder.addScript(lazyLoadAccountNameScript);
				UnloadMessageSenderScript unloadMessageSenderScript = new UnloadMessageSenderScript(
						"Are you sure you want to leave, you will loose all submitted event info!", "addEventForm");
				pageBuilder.addScript(unloadMessageSenderScript);

				renderValid(pageBuilder, user, lazyLoadAccountNameScript, query);
			}
		};

		return pageBuilder.render(user);
	}


	private void renderValid(PageBuilder pageBuilder, User user, LazyLoadAccountNameScript lazyLoadAccountNameScript, Map<String, String> query) throws SQLException {
		StringBuilder action = new StringBuilder("addEventSubmit");
		if (feedforwardQuery) {
			action.append("?");
			for (Map.Entry<String, String> entry : query.entrySet()) {
				action.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).append("&");
			}
			if (!query.isEmpty()) {  // Ran at least once - need to remove trailing "&"
				action = new StringBuilder(action.substring(0, action.length() - 1));
			}
		}

		FormBuilder form = new FormBuilder("addEventForm", FormBuilder.Method.POST, action.toString());

		form.add(new TextBuilder(TITLE) {{add("Add Event");}});

		form.add(new TextBuilder(SUBTITLE) {{add("Name");}});
		form.add(new TextBuilder(NORMAL) {{add("Please enter the event name, which should be short and concise. \n" +
				"It is shown in bold at the top of the event page and will be used when other pages are linking to this event.");}});
		form.add(new FormBuilder.TextInputBuilder("name", 1));

		form.add(new TextBuilder(SUBTITLE) {{add("Description");}});
		form.add(new TextBuilder(NORMAL) {{add("Please enter the event description, " +
				"this should go over the details of the event and add any important pieces context or details. \n" +
				"You may add fluff, however all implications (explicit and implicit) must by truthful!");}});
		form.add(new FormBuilder.TextInputBuilder("description", 5));

		form.add(new TextBuilder(SUBTITLE) {{add("Details");}});
		form.add(new TextBuilder(NORMAL) {{add("Please enter details about unknown information, or information that " +
				"needs to be added. \n" +
				"This could be about when you believe the date is, extra information in the description to be confirmed, " +
				"or just general todo + change suggestions!");}});
		form.add(new FormBuilder.TextInputBuilder("details", 5));

		form.add(new TextBuilder(SUBTITLE) {{add("Tags");}});
		form.add(new TextBuilder(NORMAL) {{
			add(" Please add tags that relate to this event. The ");
			add("tag page", "tags", true);
			add(" can link to tag descriptions.\n" +
				"If you think a new tag (or multiple) should be added, please put it in details.");}});
		makeTagSelector(form);

		form.add(new TextBuilder(SUBTITLE) {{add("People");}});
		form.add(new TextBuilder(NORMAL) {{add("""
				Please add all the people that were in this event.
				If someone that does not exist participated, please put it in details.""");}});
		form.add(new BreakBuilder());
		makePersonSelector(form, pageBuilder, lazyLoadAccountNameScript);

		form.add(new TextBuilder(SUBTITLE) {{add("Events");}});
		form.add(new TextBuilder(NORMAL) {{
			add("Please add all the other events that are related to this event.\n" +
				"Using the ");
			add("timeline", "timeline", true);
			add(", click on an event and copy the eid (text in grey under the title) and paste it in this box, " +
				"separate values with commas - e.g. 32,12,532,2");
		}});
		form.add(new FormBuilder.TextInputBuilder("events", 1, "^([0-9]*?,)*?[0-9]*$"));

		form.add(new TextBuilder(SUBTITLE) {{add("Date");}});
		form.add(new TextBuilder(NORMAL) {{
			add("""
					Please add when this event happened, there are two types of dates - "centered", and "between" - which are used to clarify uncertainty.
					Centered dates have a center which has a precision, meaning it was somewhere on that date to the precision given. It also has a difference and a difference type, show how far either side the event could occurred.
					Between dates mean that the event could've happened anywhere between the first and second date.""");
		}});
		form.add(new FormBuilder.DateInfoInputBuilder());

		form.add(new TextBuilder(SUBTITLE) {{add("Submit");}});
		String mcName = sessionHandler.getNameOf(user, query);
		form.add(new TextBuilder(NORMAL, "\n") {{
			add("This event will be submitted under the user " + mcName + ".");
			//add("An administrator will then look over the event before making it public or contacting you over any modifications or clarifications.");
			// That message is planned to be added with event screening in v2
		}});
		form.add(new FormBuilder.SubmitButtonBuilder());

		pageBuilder.add(form);
	}

	private void makeTagSelector(FormBuilder form) throws SQLException {
		Set<TagLink> tagLinks = dispatcher.getAllTagLinks();

		ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");
		for (TagLink tagLink : tagLinks) {
			tagContainer.add(new SelectableTagBuilder(tagLink.name(), tagLink.id(), tagLink.color()));
		}
		form.add(tagContainer);
		form.add(new BreakBuilder());
	}

	private void makePersonSelector(FormBuilder form, PageBuilder pageBuilder, LazyLoadAccountNameScript lazyLoadAccountNameScript) throws SQLException {
		Set<PersonLink> personLinks = dispatcher.getAllPersonLinks();

		ContainerWidgetBuilder container = new ContainerWidgetBuilder("add-person-container");
		for (PersonLink personLink : personLinks) {

			TextBuilder personNameText = new TextBuilder(NORMAL, "\n");
			PageVariable pageVariable = pageBuilder.addVariable("account-name-for-" + personLink.data().personData());
			lazyLoadAccountNameScript.add(personLink.data(), pageVariable);
			personNameText.add(pageVariable.toString());

			CheckBoxBuilder checkBoxBuilder = new CheckBoxBuilder(personNameText, "person" + personLink.id());

			container.add(checkBoxBuilder);
		}
		form.add(container);
		form.add(new BreakBuilder());
	}

	private void renderInvalidIp(PageBuilder pageBuilder, User user) {
		pageBuilder.add(new TextBuilder(TITLE) {{add("Invalid Ip");}});
		pageBuilder.add(new TextBuilder(NORMAL) {{add("Your ip is different to what was expected, for spam prevention we" +
				"require the ip of the player to be the same as the ip of the web browser unless explicitly stated.");}});
		if (user.address() != null) {
			pageBuilder.add(new TextBuilder(NORMAL) {{
				add("If you would like to continue anyway, please run this command:");
			}});
			pageBuilder.add(new TextBuilder(CODE) {{
				add("/addevent --ignore-ip");
			}});
		}
	}

	private void renderNoSession(PageBuilder pageBuilder) {
		pageBuilder.add(new TextBuilder(TITLE) {{add("Invalid Session");}});
		pageBuilder.add(new TextBuilder(NORMAL) {{add("We could not verify you as a player, please run this command in game:");}});
		pageBuilder.add(new TextBuilder(CODE) {{add("/addevent");}});
	}
}
