package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.flexiDateTime.CenteredFlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.RangedFlexiDate;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AddEventSubmitPageRenderer extends HtmlPageRenderer {
	private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
			boolean consumed = sessionHandler.suggestConsumeVerification(user, query);

			SubmittedEvent submittedEvent = SubmittedEvent.fromPost(user.post(), postedBy);
			int eid = dispatcher.addEvent(submittedEvent);

			if (consumed) {  // If it has been consumed, there is nothing to do but redirect them to the added event
				return "<html><body><p>You are being redirected, please wait...</p></body>" +
						"<script>window.location.href = 'event?id=" + eid + "';</script></html>";

			} else {  // If it has not been consumed, they might want to add an event again, so give them the option
				String getPostAgainHref = sessionHandler.getPostUrl(postedBy);  // Gets the href for posting again
				return "<html><body><p>" +
						"The event has been added, would you like to " +
						"<a href=\"event?id=" + eid + "\" target=\"_blank\">see it</a> or " +  // target="_blank" means open new tab
						"<a href=\"" + getPostAgainHref + "\" target=\"_blank\">add a new event</a>" +
						"</p></body></html>";
			}

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
			String stringTimeOffset = query.get("offset");
			String datec = query.get("datec");
			String datecUnits = query.get("datecUnits");
			String datecDiff = query.get("datecDiff");
			String dater1 = query.get("dater1");
			String dater2 = query.get("dater2");

			if (name == null || description == null || details == null || sEventIds == null || dateType == null ||
					datecUnits == null || dater1 == null || datecDiff == null || dater2 == null ||
					stringTimeOffset == null) {
				throw new RuntimeException("Got a null value");
			}

			name = name.strip();
			description = description.strip();
			details = details.strip();

			if (details.isBlank()) {
				details = null;
			}

			Set<Integer> eventIds;
			if (!sEventIds.isEmpty()) {
				eventIds = Arrays.stream(sEventIds.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toSet());
			} else {
				eventIds = Collections.emptySet();
			}

			int timeOffset = timeToMins(stringTimeOffset);

			FlexiDateTime flexiDateTime;
			if (dateType.equals("Centered")) {
				LocalDateTime dateTime = getLDT(datec);
				CenteredFlexiDateTime.Units units = CenteredFlexiDateTime.Units.decode(datecUnits);
				long diff = Long.parseLong(datecDiff);

				dateTime = dateTime.truncatedTo(units.temporalUnit); // Hours + Minutes only exist as the html form has to have them, so remove them here if need be
				long center = dateTime.toEpochSecond(ZoneOffset.UTC) / units.value;  // Can use UTC as have offset

				flexiDateTime = new CenteredFlexiDateTime(center, units, diff, timeOffset);

			} else if (dateType.equals("Ranged")) {
				LocalDateTime date1ldt = getLDT(dater1 + "T00:00");  // Add T00:00 to disguise it as a datetime
				LocalDateTime date2ldt = getLDT(dater2 + "T00:00");

				// No need for truncation as is already in days

				long date1 = date1ldt.toEpochSecond(ZoneOffset.UTC) / (24 * 60 * 60);  // Convert to days since epoch
				long date2 = date2ldt.toEpochSecond(ZoneOffset.UTC) / (24 * 60 * 60);  // Can use UTC as have offset

				flexiDateTime = new RangedFlexiDate(date1, date2, timeOffset);

			} else {
				throw new RuntimeException("Unknown date type \""  + dateType + "\"");
			}

			return new SubmittedEvent(name, description, details, tagIds, personIds, eventIds, flexiDateTime, postedBy);
		}

		private static int timeToMins(String stringTime) {
			if (!(stringTime.startsWith("+") || stringTime.startsWith("-"))) stringTime = "+" + stringTime;

			boolean isPositive = stringTime.charAt(0) == '+';

			String[] parts = stringTime.substring(1).split(":");
			int mins = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
			mins = mins * (isPositive ? 1 : -1);

			return mins;
		}

		/**
		 * Gets the {@link java.time.LocalDateTime} from a html dateTime (with
		 * minute precision) and a timezone.
		 */
		 private static LocalDateTime getLDT(String input) {
			 char[] chars = input.toCharArray();
			 chars[10] = ' ';
			 String fullDateTimeString = new String(chars);
			 return LocalDateTime.parse(fullDateTimeString, dateTimeFormat);
		 }
	}
}
