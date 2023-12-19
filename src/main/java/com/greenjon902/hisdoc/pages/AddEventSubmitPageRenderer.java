package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.flexiDateTime.CenteredFlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.RangedFlexiDate;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AddEventSubmitPageRenderer extends HtmlPageRenderer {
	private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd z");

	private final Dispatcher dispatcher;
	private final SessionHandler sessionHandler;

	public AddEventSubmitPageRenderer(Dispatcher dispatcher, SessionHandler sessionHandler) {
		this.dispatcher = dispatcher;
		this.sessionHandler = sessionHandler;
	}

	@Override
	public String render(Map<String, String> query, String fragment, User user) throws SQLException {
		try {
			if (sessionHandler.verify(user, query) != SessionHandler.VerifyResult.VALID) {
				throw new IllegalStateException("You are not verified, you should not be on this page!");
			}
			int postedBy = sessionHandler.getPersonId(user, query);
			sessionHandler.suggestConsumeVerification(user, query);

			SubmittedEvent submittedEvent = SubmittedEvent.fromPost(user.post(), postedBy);
			int eid = dispatcher.addEvent(submittedEvent);
			return "<html><script>window.location.href = 'event?id=" + eid + "';</script><html>";

		} catch (Exception e) {
			throw new RuntimeException("An error occurred,\nquery=\n" + query + "\n\n", e);
		}
	}

	public record SubmittedEvent(String name, String description, String details, Set<Integer> tagIds,
								 Set<Integer> personIds, Set<Integer> relatedEventIds, FlexiDateTime dateInfo, int postedBy) {
		public SubmittedEvent(String name, String description, String details, Set<Integer> tagIds, Set<Integer> personIds, Set<Integer> relatedEventIds, FlexiDateTime dateInfo, int postedBy) {
			this.name = name;
			this.description = description;
			this.details = details;
			this.tagIds = Collections.unmodifiableSet(tagIds);
			this.personIds = Collections.unmodifiableSet(personIds);
			this.relatedEventIds = Collections.unmodifiableSet(relatedEventIds);
			this.dateInfo = dateInfo;
			this.postedBy = postedBy;
		}

		public static @NotNull SubmittedEvent fromPost(@NotNull Map<String, String> query, int postedBy) {
			String name = query.get("name");
			String description = query.get("description");
			String details = query.get("details");

			HashSet<Integer> personIds = new HashSet<>();
			HashSet<Integer> tagIds = new HashSet<>();
			for (String key : query.keySet()) {
				if (key.startsWith("person") && query.get(key).equals("on")) {
					personIds.add(Integer.valueOf(key.replace("person", "")));
				} else if (key.startsWith("tag") && query.get(key).equals("on")) {
					tagIds.add(Integer.valueOf(key.replace("tag", "")));
				}
			}

			String sEventIds = query.get("events");

			String dateType = query.get("dateType");
			String timezone = query.get("timezone");
			String datec = query.get("datec");
			String datecUnits = query.get("datecUnits");
			String datecDiff = query.get("datecDiff");
			String dater1 = query.get("dater1");
			String dater2 = query.get("dater2");

			if (name == null || description == null || details == null || sEventIds == null || dateType == null ||
					datecUnits == null || dater1 == null || datecDiff == null || dater2 == null ||
					timezone == null) {
				throw new RuntimeException("Got a null value");
			}

			if (details.isBlank()) {
				details = null;
			}

			Set<Integer> eventIds;
			if (!sEventIds.isEmpty()) {
				eventIds = Arrays.stream(sEventIds.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toSet());
			} else {
				eventIds = Collections.emptySet();
			}

			FlexiDateTime flexiDateTime;

			if (dateType.equals("Centered")) {
				ZonedDateTime zonedDateTime = getZDT(datec, timezone);
				CenteredFlexiDateTime.Units units = CenteredFlexiDateTime.Units.decode(datecUnits);
				long diff = Long.parseLong(datecDiff);

				zonedDateTime = zonedDateTime.truncatedTo(units.temporalUnit); // Hours + Minutes only exist as the html form has to have them, so remove them here if need be
				long center = zonedDateTime.toEpochSecond() / units.value;

				flexiDateTime = new CenteredFlexiDateTime(center, units, diff);

			} else if (dateType.equals("Ranged")) {
				ZonedDateTime date1zdt = getZDT(dater1 + "T00:00", timezone);  // Add T00:00 to disguise it as a datetime
				ZonedDateTime date2zdt = getZDT(dater2 + "T00:00", timezone);

				// No need for truncation as is already in days

				long date1 = date1zdt.toEpochSecond() / (24 * 60 * 60);  // Convert to days since epoch
				long date2 = date2zdt.toEpochSecond() / (24 * 60 * 60);

				flexiDateTime = new RangedFlexiDate(date1, date2);

			} else {
				throw new RuntimeException("Unknown date type \""  + dateType + "\"");
			}

			return new SubmittedEvent(name, description, details, tagIds, personIds, eventIds, flexiDateTime, postedBy);
		}

		/**
		 * Gets the {@link ZonedDateTime} from a html dateTime (with
		 * minute precision) and a timezone.
		 */
		 private static ZonedDateTime getZDT(String input, String timezone) {
			 char[] chars = input.toCharArray();
			 chars[10] = ' ';
			 String fullDateTimeString = new String(chars) + " " + timezone;
			 return ZonedDateTime.parse(fullDateTimeString, dateTimeFormat);
		 }
	}
}
