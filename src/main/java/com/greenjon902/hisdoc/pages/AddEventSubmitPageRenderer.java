package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.DateInfo;
import com.greenjon902.hisdoc.webDriver.PageRenderer;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class AddEventSubmitPageRenderer extends PageRenderer {
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
								 Set<Integer> personIds, Set<Integer> relatedEventIds, DateInfo dateInfo, int postedBy) {
		public SubmittedEvent(String name, String description, String details, Set<Integer> tagIds, Set<Integer> personIds, Set<Integer> relatedEventIds, DateInfo dateInfo, int postedBy) {
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
			String datec1 = query.get("datec1");
			String datecPrecision = query.get("datecPrecision");
			String datecDiff = query.get("datecDiff");
			String datecDiffType = query.get("datecDiffType");
			String dateb1 = query.get("dateb1");
			String dateb2 = query.get("dateb2");

			if (name == null || description == null || details == null || sEventIds == null || dateType == null ||
					datec1 == null || datecPrecision == null || datecDiff == null || datecDiffType == null ||
					dateb1 == null || dateb2 == null) {
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

			Timestamp center = Timestamp.valueOf(convertTimestampSeparators(datec1));
			Timestamp start = new Timestamp(Date.valueOf(dateb1).getTime());
			Date end = Date.valueOf(dateb2);

			DateInfo dateInfo = switch (DateInfo.Type.decode(dateType)) {
				case CENTERED -> DateInfo.centered(center, DateInfo.Precision.decode(datecPrecision), Integer.parseInt(datecDiff), DateInfo.Precision.decode(datecDiffType));
				case BETWEEN -> DateInfo.between(start, end);
			};


			return new SubmittedEvent(name, description, details, tagIds, personIds, eventIds, dateInfo, postedBy);
		}
		 private static String convertTimestampSeparators(String input) {
			 char[] chars = input.toCharArray();
			 chars[10] = ' ';
			 chars[13] = ':';
			 chars[16] = ':';
			 return new String(chars);
		 }
	}
}
