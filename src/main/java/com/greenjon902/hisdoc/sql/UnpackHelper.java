package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.flexiDateTime.CenteredFlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.RangedFlexiDate;
import com.greenjon902.hisdoc.sql.results.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MINUTES;

public class UnpackHelper {
	/**
	 * Unpacks a singular {@link PersonData} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns personType, and personData.
	 */
	public static PersonData getPersonData(ResultSet result) throws SQLException {
		return new PersonData(result.getString("personType"), result.getString("personData"));
	}

	/**
	 * Unpacks a singular {@link ChangeInfo} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns date, authorPid, authorInfo, and description.
	 */
	public static ChangeInfo getChangeInfo(ResultSet result) throws SQLException {
		return new ChangeInfo(new CenteredFlexiDateTime(result.getLong("date"), CenteredFlexiDateTime.Units.MINUTE, 0),
				new PersonLink(result.getInt("authorPid"), getPersonData(result)),
				result.getString("description"));
	}

	/**
	 * Unpacks a singular {@link FlexiDateTime} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eventDateType, eventDate1, eventDateUnits, eventDateDiff and
	 * eventDate2.
	 */
	public static FlexiDateTime getFlexiDateTime(ResultSet result) throws SQLException {
		String type = result.getString("eventDateType");

		if (type.equals("c")) {
			long center = result.getLong("eventDate1");
			long diff = result.getLong("eventDateDiff");
			CenteredFlexiDateTime.Units units = CenteredFlexiDateTime.Units.decode(result.getString("eventDateUnits"));

			return new CenteredFlexiDateTime(center, units, diff);

		} else if (type.equals("r")) {
			long date1 = result.getLong("eventDate1");
			long date2 = result.getLong("eventDate2");

			return new RangedFlexiDate(date1, date2);

		} else {
			throw new RuntimeException("Unknown date type \""  + type + "\"");
		}
	}

	/**
	 * Unpacks a singular {@link EventLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eid, name.
	 */
	public static EventLink getEventLink(ResultSet result) throws SQLException {
		return new EventLink(result.getInt("eid"), result.getString("name"), getFlexiDateTime(result),
				result.getString("description"));
	}

	/**
	 * Unpacks a singular {@link TagLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eid, name.
	 */
	public static TagLink getTagLink(ResultSet result) throws SQLException {
		return new TagLink(result.getInt("tid"), result.getString("name"), result.getInt("color"));
	}

	/**
	 * Unpacks a singular {@link PersonLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns pid, personInfo.
	 */
	public static PersonLink getPersonLink(ResultSet result) throws SQLException {
		return new PersonLink(result.getInt("pid"), getPersonData(result));
	}

	/**
	 * Unpacks multiple {@link TagLink}s from a {@link ResultSet}, this expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 * This looks at the columns used by {@link #getTagLink(ResultSet)} and count.
	 */
	public static Map<TagLink, Integer> getCountedTagLinks(ResultSet result) throws SQLException {
		Map<TagLink, Integer> countedTagLinks = new HashMap<>();
		while (result.next()) {
			countedTagLinks.put(getTagLink(result), result.getInt("count"));
		}
		return countedTagLinks;
	}

	/**
	 * Unpacks a single {@link EventInfo} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public static EventInfo getEventInfo(PreparedStatement ps) throws SQLException {
		Set<TagLink> tagLinks = getSet(ps.getResultSet(), UnpackHelper::getTagLink);
		nextResultSet(ps, "personLinks");
		Set<PersonLink> personLinks = getSet(ps.getResultSet(), UnpackHelper::getPersonLink);
		nextResultSet(ps, "eventLinks");
		Set<EventLink> eventLinks = getSet(ps.getResultSet(), UnpackHelper::getEventLink);
		nextResultSet(ps, "changeInfos");
		List<ChangeInfo> changeInfos = UnpackHelper.getList(ps.getResultSet(), UnpackHelper::getChangeInfo);

		nextResultSet(ps, "event");
		ResultSet result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}
		FlexiDateTime eventDateInfo = getFlexiDateTime(result);

		Integer postedPid;
		PersonLink postedPerson = null;
		if ((postedPid = getInteger(result, "postedPid")) != null) {
			postedPerson = new PersonLink(postedPid, getPersonData(result));
		}

		Long postedDateLong = getLong(result, "postedDate");
		FlexiDateTime postedDate;
		if (postedDateLong == null) {
			postedDate = null;
		} else {
			postedDate = new CenteredFlexiDateTime(postedDateLong, CenteredFlexiDateTime.Units.MINUTE, 0);
		}

		return new EventInfo(
				result.getInt("eid"),
				result.getString("name"),
				result.getString("description"),
				postedDate,
				postedPerson,
				eventDateInfo,
				tagLinks,
				personLinks,
				changeInfos,
				eventLinks,
				result.getString("details")
		);
	}

	/**
	 * Unpacks a single {@link PersonLink} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public static PersonInfo getPersonInfo(PreparedStatement ps) throws SQLException {
		Map<TagLink, Integer> countedTagLinks = getCountedTagLinks(ps.getResultSet());

		nextResultSet(ps, "postCount");
		ResultSet result = ps.getResultSet();
		next(result, "postCount", "the");
		int postCount = ps.getResultSet().getInt("count");

		nextResultSet(ps, "eventCount");
		result = ps.getResultSet();
		next(result, "eventCount", "the");
		int eventCount = ps.getResultSet().getInt("count");

		nextResultSet(ps, "recentEvents");
		List<EventLink> recentEvents = getList(ps.getResultSet(), UnpackHelper::getEventLink);

		nextResultSet(ps, "recentPosts");
		List<EventLink> recentPosts = getList(ps.getResultSet(), UnpackHelper::getEventLink);

		nextResultSet(ps, "person");
		result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}

		return new PersonInfo(
				result.getInt("pid"),
				getPersonData(result),
				countedTagLinks,
				postCount, eventCount, recentEvents, recentPosts);
	}

	/**
	 * Unpacks a single {@link TagInfo} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public static TagInfo getTagInfo(PreparedStatement ps) throws SQLException {
		List<EventLink> recentEventLinks = getList(ps.getResultSet(), UnpackHelper::getEventLink);
		nextResultSet(ps, "tagInfo");
		ResultSet result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}

		return new TagInfo(
				result.getInt("tid"),
				result.getString("name"),
				result.getString("description"),
				result.getInt("color"),
				recentEventLinks
		);
	}

	public static <T> List<T> getList(ResultSet result, CheckedFunction<ResultSet, T> getter) throws SQLException {
		return getCollection(result, getter, ArrayList::new);
	}

	public static <T> Set<T> getSet(ResultSet result, CheckedFunction<ResultSet, T> getter) throws SQLException {
		return getCollection(result, getter, HashSet::new);
	}

	/**
	 * Gets a collection from the sql. This expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 * @param result The {@link ResultSet} to get values from
	 * @param getter The function to call on the result set to get the current item in the collection
	 * @param collectionGenerator A function to create a new instance of the desired collection
	 * @return The collection
	 * @param <T> The type that will fill the collection
	 * @param <U> The type of collection
	 */
	private static <T, U extends Collection<T>> U getCollection(ResultSet result, CheckedFunction<ResultSet, T> getter, Callable<U> collectionGenerator) throws SQLException {
		U collection;
		try {
			collection = collectionGenerator.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		while (result.next()) {
			if (!collection.add(getter.apply(result))) throw new RuntimeException("Unexpected return of false from adding to collection, this could be duplicate in a set?");
		}
		return collection;
	}

	/**
	 * Moves the {@link PreparedStatement} to the next {@link ResultSet}, throwing an error if it cannot be found.
	 * If we do not require the next result set, as it is optional, then use {@link PreparedStatement#getMoreResults()}.
	 * @param type A string of what the result set should hold, this is used for error formatting
	 */
	public static void nextResultSet(PreparedStatement ps, String type) throws SQLException {
		if (!ps.getMoreResults()) {
			throw new RuntimeException("Could not find results for " + type);
		}
	}

	/**
	 * Moves the {@link ResultSet} to the next result, throwing an error if it cannot be found.
	 * If we do not require the next result, as it is optional, then use {@link ResultSet#next()}.
	 * @param type A string of what the result set should hold, this is used for error formatting
	 * @param number A string of where the next result will be (e.g. "next", or "first")
	 */
	public static void next(ResultSet result, String type, String number) throws SQLException {
		if (!result.next()) {
			throw new RuntimeException("Could not find " + number + " result for " + type);
		}
	}

	/**
	 * A helper function to get a nullable integer from sql.
	 */
	public static Integer getInteger(ResultSet result, String name) throws SQLException {
		Integer integer = result.getInt(name);
		if (result.wasNull()) {
			integer = null;
		}
		return integer;
	}

	/**
	 * A helper function to get a nullable long from sql.
	 */
	public static Long getLong(ResultSet result, String name) throws SQLException {
		Long loong = result.getLong(name);
		if (result.wasNull()) {
			loong = null;
		}
		return loong;
	}

	/**
	 * Unpacks multiple {@link EventInfo} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public static TimelineInfo getTimelineInfo(PreparedStatement ps) throws SQLException {
		List<EventLink> eventLinks = getList(ps.getResultSet(), UnpackHelper::getEventLink);
		nextResultSet(ps, "tagLinks");
		Set<TagLink> tagLinks = getSet(ps.getResultSet(), UnpackHelper::getTagLink);
		nextResultSet(ps, "personLinks");
		Set<PersonLink> personLinks = getSet(ps.getResultSet(), UnpackHelper::getPersonLink);
		nextResultSet(ps, "eventTagRelations");
		HashMap<EventLink, ArrayList<TagLink>> eventTagRelations = getRelationsFromRaw(ps.getResultSet(), "eid", "tid", eventLinks, tagLinks);
		nextResultSet(ps, "eventPersonRelations");
		HashMap<EventLink, ArrayList<PersonLink>> eventPersonRelations = getRelationsFromRaw(ps.getResultSet(), "eid", "pid", eventLinks, personLinks);


		return new TimelineInfo(eventLinks, tagLinks, personLinks, eventTagRelations, eventPersonRelations);
	}

	/**
	 * Unpacks multiple id sets from a {@link ResultSet}, this expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 * This returns a hash map, where the key is the first id, and the value is a list of all the values for the given key.
	 */
	public static <K extends Idable, V extends Idable> HashMap<K, ArrayList<V>> getRelationsFromRaw(ResultSet resultSet, String keyName, String valueName, Collection<K> keyValues, Collection<V> valueValues) throws SQLException {
		HashMap<Integer, K> keyMap = new HashMap<>();
		keyValues.forEach(k -> keyMap.put(k.id(), k));
		HashMap<Integer, V> valueMap = new HashMap<>();
		valueValues.forEach(v -> valueMap.put(v.id(), v));

		HashMap<K, ArrayList<V>> relations = new HashMap<>();
		while (resultSet.next()) {
			K key = keyMap.get(resultSet.getInt(keyName));
			V value = valueMap.get(resultSet.getInt(valueName));
			relations.putIfAbsent(key, new ArrayList<>());
			relations.get(key).add(value);
		}
		return relations;
	}
}

@FunctionalInterface
interface CheckedFunction<T, R> {
	R apply(T t) throws SQLException;
}

@FunctionalInterface
interface CheckedCallable<T> {
	T call() throws SQLException;
}
