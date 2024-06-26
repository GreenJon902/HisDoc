package com.greenjon902.hisdoc.pages.eventModification;

import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.pageBuilder.widgets.*;
import com.greenjon902.hisdoc.pages.HtmlPageRenderer;
import com.greenjon902.hisdoc.person.MinecraftPerson;
import com.greenjon902.hisdoc.person.Person;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.EventInfo;
import com.greenjon902.hisdoc.sql.results.EventLink;
import com.greenjon902.hisdoc.sql.results.PersonLink;
import com.greenjon902.hisdoc.sql.results.TagLink;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.*;
import static com.greenjon902.hisdoc.pageBuilder.widgets.TextType.NORMAL;

/**
 * Base for {@link AddEventPageRenderer} and {@link EditEventPageRenderer}.
 * We call it the modify base, as adding is basically modifying nothing? It made more sense than modifying extending
 * adding to me.
 */
public abstract class AbstractModifyEventPageRenderer extends HtmlPageRenderer {
	protected final Dispatcher dispatcher;
	protected final PermissionHandler permissionHandler;
	protected final SessionHandler sessionHandler;

	public AbstractModifyEventPageRenderer(Dispatcher dispatcher, PermissionHandler permissionHandler, SessionHandler sessionHandler) {
		this.dispatcher = dispatcher;
		this.permissionHandler = permissionHandler;
		this.sessionHandler = sessionHandler;
	}

	/**
	 * Renders the form elements to do with the event, so names, tags, descriptions, dates, etc.
	 * Note: This does not include the submit button.
	 * @param defaultContents If not null, form elements will be prefilled with data from this
	 * @param form The formBuilder to add the form elements to
	 */
	protected void renderEventFormItems(FormBuilder form, EventInfo defaultContents) throws SQLException {
		// When modifying an event, we need to prefill the boxes
		String defaultName;
		String defaultDescription;
		String defaultDetails;
		String defaultRelatedEvents;
		Set<Integer> defaultRelatedTags;
		Set<Integer> defaultRelatedPersons;
		FlexiDateTime defaultDate;

		if (defaultContents == null) {  // No prexisiting info so set everything to a default value
			defaultName = "";
			defaultDescription = "";
			defaultDetails = "";
			defaultRelatedEvents = "";
			defaultRelatedTags = Collections.emptySet();
			defaultRelatedPersons = Collections.emptySet();
			defaultDate = null;

		} else {
			defaultName = defaultContents.name();
			defaultDescription = defaultContents.description();
			defaultDetails = defaultContents.details();
			if (defaultDetails == null) {
				defaultDetails = "";
			}

			// We need a string of ids for related events
			ArrayList<Integer> relatedEventIds = new ArrayList<>(defaultContents.relatedEventLinks().size());
			for (EventLink eventLink : defaultContents.relatedEventLinks()) {
				relatedEventIds.add(eventLink.id());
			}
			relatedEventIds.sort(Integer::compareTo);
			defaultRelatedEvents = relatedEventIds.stream().map((Function<Integer, CharSequence>) String::valueOf).collect(Collectors.joining(","));

			defaultRelatedTags = defaultContents.tagLinks().stream().map(TagLink::id).collect(Collectors.toSet());
			defaultRelatedPersons = defaultContents.relatedPersonLinks().stream().map(PersonLink::id).collect(Collectors.toSet());

			defaultDate = defaultContents.eventDateInfo();
		}

		form.add(new TextBuilder(SUBTITLE) {{add("Name");}});
		form.add(new TextBuilder(NORMAL) {{add("Please enter the event name, which should be short and concise. \n" +
				"It is shown in bold at the top of the event page and will be used when other pages are linking to this event.");}});
		form.add(new FormBuilder.TextInputBuilder("name", 1, ".*", defaultName, true));  // Pattern allows anything but newlines, it also locks it to one line anyway due to html stuffs

		form.add(new TextBuilder(SUBTITLE) {{add("Description");}});
		form.add(new TextBuilder(NORMAL) {{add("Please enter the event description, " +
				"this should go over the details of the event and add any important pieces context or details. \n" +
				"You may add fluff, however all implications (explicit and implicit) must by truthful!");}});
		form.add(new FormBuilder.TextInputBuilder("description", 5, defaultDescription, true));

		form.add(new TextBuilder(SUBTITLE) {{add("Details");}});
		form.add(new TextBuilder(NORMAL) {{add("Please enter details about unknown information, or information that " +
				"needs to be added. \n" +
				"This could be about when you believe the date is, extra information in the description to be confirmed, " +
				"or just general todo + change suggestions!");}});
		form.add(new FormBuilder.TextInputBuilder("details", 5, defaultDetails, false));

		form.add(new TextBuilder(SUBTITLE) {{add("Tags");}});
		form.add(new TextBuilder(NORMAL) {{
			add(" Please add tags that relate to this event. The ");
			add("tag page", "tags", true);
			add(" can link to tag descriptions.\n" +
					"If you think a new tag (or multiple) should be added, please put it in details.");}});
		makeTagSelector(form, defaultRelatedTags);

		form.add(new TextBuilder(SUBTITLE) {{add("People");}});
		form.add(new TextBuilder(NORMAL) {{add("""
				Please add all the people that were in this event.
				If someone that does not exist participated, please put it in details.""");}});
		form.add(new BreakBuilder());
		makePersonSelector(form, defaultRelatedPersons);

		form.add(new TextBuilder(SUBTITLE) {{add("Events");}});
		form.add(new TextBuilder(NORMAL) {{
			add("Please add all the other events that are related to this event.\n" +
					"Using the ");
			add("timeline", "timeline", true);
			add(", click on an event and copy the eid (text in grey under the title) and paste it in this box, " +
					"separate values with commas - e.g. 32,12,532,2");
		}});
		form.add(new FormBuilder.TextInputBuilder("events", 1, "^([0-9]*?,)*?[0-9]*$", defaultRelatedEvents, false));

		form.add(new TextBuilder(SUBTITLE) {{add("Date");}});
		form.add(new TextBuilder(NORMAL) {{
			add("""
					Please add when this event happened, there are two types of dates - "centered", and "between" - which are used to clarify uncertainty.
					Centered dates have a center which has a precision, meaning it was somewhere on that date to the precision given. It also has a difference and a difference type, show how far either side the event could occurred.
					Between dates mean that the event could've happened anywhere between the first and second date.""");
		}});
		form.add(new FormBuilder.FlexiDateTimeInputBuilder(defaultDate));
	}

	protected String getNameOf(int pid) throws SQLException {
		Person person = dispatcher.getPersonInfo(pid).person();
		String name = person.name();
		if (person instanceof MinecraftPerson minecraftPerson) {
			name += " (" + minecraftPerson.uuid() + ")";  // Incase user is unknown
		}
		return name;
	}

	private void makeTagSelector(FormBuilder form, Set<Integer> defaultSelected) throws SQLException {
		ArrayList<TagLink> tagLinks = new ArrayList<>(dispatcher.getAllTagLinks());  // List so we can sort them
		tagLinks.sort(Comparator.comparing(o -> o.name().toLowerCase()));

		ContainerWidgetBuilder tagContainer = new ContainerWidgetBuilder("tag-container");
		for (TagLink tagLink : tagLinks) {
			boolean selected = defaultSelected.contains(tagLink.id());
			tagContainer.add(new SelectableTagBuilder(tagLink.name(), tagLink.id(), tagLink.color(),
					tagLink.description(), selected));
		}
		form.add(tagContainer);
		form.add(new BreakBuilder());
	}

	private void makePersonSelector(FormBuilder form, Set<Integer> defaultSelected) throws SQLException {
		ArrayList<PersonLink> personLinks = new ArrayList<>(dispatcher.getAllPersonLinks());  // List so we can sort them
		personLinks.sort(Comparator.comparing(o -> o.person().name().toLowerCase()));

		ContainerWidgetBuilder container = new ContainerWidgetBuilder("add-person-container", "add-person-container", "");
		for (PersonLink personLink : personLinks) {

			TextBuilder personNameText = new TextBuilder(NORMAL, "\n", null);
			personNameText.add(personLink.person().name());

			boolean selected = defaultSelected.contains(personLink.id());

			CheckBoxBuilder checkBoxBuilder = new CheckBoxBuilder(personNameText, "person" + personLink.id(),
					selected);

			container.add(checkBoxBuilder);
		}
		form.add(container);
		form.add(new BreakBuilder());
	}
}
