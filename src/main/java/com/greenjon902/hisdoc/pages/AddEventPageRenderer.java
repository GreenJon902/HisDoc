package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.pageBuilder.scripts.LazyLoadAccountNameScript;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.sessionHandler.SessionHandler;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.PersonLink;
import com.greenjon902.hisdoc.sql.results.TagLink;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;

// TODO: Saving event drafts
// TODO: Date entering widget builder
// TODO: Tag/event/user list entering widget builder

public class AddEventPageRenderer extends PageRenderer {
	private final Dispatcher dispatcher;
	private final SessionHandler sessionHandler;

	public AddEventPageRenderer(Dispatcher dispatcher, SessionHandler sessionHandler) {
		this.dispatcher = dispatcher;
		this.sessionHandler = sessionHandler;
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		PageBuilder pageBuilder = new PageBuilder();
		LazyLoadAccountNameScript lazyLoadAccountNameScript = new LazyLoadAccountNameScript();
		pageBuilder.addScript(lazyLoadAccountNameScript);

		pageBuilder.add(new NavBarBuilder(pageBuilder));

		switch (sessionHandler.verify(user)) {
			case NO_SESSION -> renderNoSession(pageBuilder);
			case INVALID_IP -> renderInvalidIp(pageBuilder, user);
			case VALID -> renderValid(pageBuilder, user, lazyLoadAccountNameScript);
		};

		return pageBuilder.render(user);
	}

	private void renderValid(PageBuilder pageBuilder, User user, LazyLoadAccountNameScript lazyLoadAccountNameScript) throws SQLException {
		FormBuilder form = new FormBuilder();

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
			add(" Please add all the other events that are related to this event.\n" +
				"Using the ");
			add("timeline", "timeline", true);
			add(", click on an event and copy the eid (text in grey under the title) and paste it in this box, " +
				"separate values with commas - e.g. 32,12,532,2");
		}});
		form.add(new FormBuilder.TextInputBuilder("events", 1));

		form.add(new TextBuilder(SUBTITLE) {{add("Submit");}});
		String mcName = sessionHandler.getNameOf(user);
		form.add(new TextBuilder(NORMAL, "\n") {{
			add("This event will be submitted under the user " + mcName + ".");
			add("An administrator will then look over the event before making it public or contacting you over any modifications or clarifications.");
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

			CheckBoxBuilder checkBoxBuilder = new CheckBoxBuilder(personNameText, "user" + personLink.id());

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
				add("/hs verify --ip " + user.address().getHostString());
			}});
		}
		pageBuilder.add(new TextBuilder(NORMAL) {{
			add("Or to allow any ip, please run this command:");
		}});
		pageBuilder.add(new TextBuilder(CODE) {{
			add("/hs verify --noip ");
		}});
	}

	private void renderNoSession(PageBuilder pageBuilder) {
		pageBuilder.add(new TextBuilder(TITLE) {{add("Invalid Session");}});
		pageBuilder.add(new TextBuilder(NORMAL) {{add("We could not verify you as a player, please run this command in game:");}});
		pageBuilder.add(new TextBuilder(CODE) {{add("/hs addEvent");}});
	}
}
