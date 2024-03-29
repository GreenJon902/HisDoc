package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.MinecraftInfoSupplier;
import com.greenjon902.hisdoc.flexiDateTime.CenteredFlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.FlexiDateTime;
import com.greenjon902.hisdoc.flexiDateTime.RangedFlexiDate;
import com.greenjon902.hisdoc.person.MinecraftPerson;
import com.greenjon902.hisdoc.person.MiscellaneousPerson;
import com.greenjon902.hisdoc.person.Person;
import com.greenjon902.hisdoc.person.PersonType;
import com.greenjon902.hisdoc.sql.results.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

public class UnpackHelper {
	private final MinecraftInfoSupplier minecraftInfoSupplier;

	public UnpackHelper(MinecraftInfoSupplier minecraftInfoSupplier) {
		this.minecraftInfoSupplier = minecraftInfoSupplier;
	}

	/**
	 * Unpacks a singular {@link com.greenjon902.hisdoc.person.Person} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}). This will identify the type of person and create the
	 * appropriate class.
	 * This looks at the columns personType, and personData.
	 */
	public Person getPerson(ResultSet result) throws SQLException {
		PersonType type = PersonType.valueOf(result.getString("personType"));
		String data = result.getString("personData");

		return switch (type) {
			case MINECRAFT -> new MinecraftPerson(UUID.fromString(data), minecraftInfoSupplier);
			case MISCELLANEOUS -> new MiscellaneousPerson(data);
		};
	}

	/**
	 * Unpacks a singular {@link ChangeInfo} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns date, authorPid, authorInfo, and description.
	 */
	public ChangeInfo getChangeInfo(ResultSet result) throws SQLException {
		return new ChangeInfo(
				new CenteredFlexiDateTime(result.getLong("date"), CenteredFlexiDateTime.Units.SECOND, 0, 0),
				new PersonLink(result.getInt("authorPid"), getPerson(result)),
				result.getString("description"));
	}

	/**
	 * Unpacks a singular {@link FlexiDateTime} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eventDateType, eventDate1, eventDateUnits, eventDateDiff and
	 * eventDate2.
	 */
	public FlexiDateTime getFlexiDateTime(ResultSet result) throws SQLException {
		String type = result.getString("eventDateType");

		if (type.equals("c")) {
			long center = result.getLong("eventDate1");
			long diff = result.getLong("eventDateDiff");
			int offset = result.getInt("eventDateTimeOffset");
			CenteredFlexiDateTime.Units units = CenteredFlexiDateTime.Units.decode(result.getString("eventDateUnits"));

			return new CenteredFlexiDateTime(center, units, diff, offset);

		} else if (type.equals("r")) {
			long date1 = result.getLong("eventDate1");
			long date2 = result.getLong("eventDate2");
			int offset = result.getInt("eventDateTimeOffset");

			return new RangedFlexiDate(date1, date2, offset);

		} else {
			throw new RuntimeException("Unknown date type \""  + type + "\"");
		}
	}

	/**
	 * Unpacks a singular {@link EventLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eid, name.
	 */
	public EventLink getEventLink(ResultSet result) throws SQLException {
		return new EventLink(result.getInt("eid"), result.getString("name"), getFlexiDateTime(result),
				result.getString("description"));
	}

	/**
	 * Unpacks a singular {@link TagLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eid, name.
	 */
	public TagLink getTagLink(ResultSet result) throws SQLException {
		return new TagLink(
				result.getInt("tid"),
				result.getString("name"),
				result.getInt("color"),
				result.getString("description"));
	}

	/**
	 * Unpacks a singular {@link PersonLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns pid, personInfo.
	 */
	public PersonLink getPersonLink(ResultSet result) throws SQLException {
		return new PersonLink(result.getInt("pid"), getPerson(result));
	}

	/**
	 * Unpacks multiple {@link TagLink}s from a {@link ResultSet}, this expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 * This looks at the columns used by {@link #getTagLink(ResultSet)} and count.
	 */
	public Map<TagLink, Integer> getCountedTagLinks(ResultSet result) throws SQLException {
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
	public EventInfo getEventInfo(PreparedStatement ps) throws SQLException {
		Set<TagLink> tagLinks = getSet(ps.getResultSet(), this::getTagLink);
		nextResultSet(ps, "personLinks");
		Set<PersonLink> personLinks = getSet(ps.getResultSet(), this::getPersonLink);
		nextResultSet(ps, "eventLinks");
		Set<EventLink> eventLinks = getSet(ps.getResultSet(), this::getEventLink);
		nextResultSet(ps, "changeInfos");
		List<ChangeInfo> changeInfos = getList(ps.getResultSet(), this::getChangeInfo);

		nextResultSet(ps, "event");
		ResultSet result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}
		FlexiDateTime eventDateInfo = getFlexiDateTime(result);

		Integer postedPid;
		PersonLink postedPerson = null;
		if ((postedPid = getInteger(result, "postedPid")) != null) {
			postedPerson = new PersonLink(postedPid, getPerson(result));
		}

		Long postedDateLong = getLong(result, "postedDate");
		FlexiDateTime postedDate;
		if (postedDateLong == null) {
			postedDate = null;
		} else {
			postedDate = new CenteredFlexiDateTime(postedDateLong, CenteredFlexiDateTime.Units.SECOND, 0, 0);
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
	public PersonInfo getPersonInfo(PreparedStatement ps) throws SQLException {
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
		List<EventLink> recentEvents = getList(ps.getResultSet(), this::getEventLink);

		nextResultSet(ps, "recentPosts");
		List<EventLink> recentPosts = getList(ps.getResultSet(), this::getEventLink);

		nextResultSet(ps, "person");
		result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}

		return new PersonInfo(
				result.getInt("pid"),
				getPerson(result),
				countedTagLinks,
				postCount, eventCount, recentEvents, recentPosts);
	}

	/**
	 * Unpacks a single {@link TagInfo} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public TagInfo getTagInfo(PreparedStatement ps) throws SQLException {
		List<EventLink> recentEventLinks = getList(ps.getResultSet(), this::getEventLink);
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

	public <T> List<T> getList(ResultSet result, CheckedFunction<ResultSet, T> getter) throws SQLException {
		return getCollection(result, getter, ArrayList::new);
	}

	public <T> Set<T> getSet(ResultSet result, CheckedFunction<ResultSet, T> getter) throws SQLException {
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
	private <T, U extends Collection<T>> U getCollection(ResultSet result, CheckedFunction<ResultSet, T> getter, Callable<U> collectionGenerator) throws SQLException {
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
	public void nextResultSet(PreparedStatement ps, String type) throws SQLException {
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
	public void next(ResultSet result, String type, String number) throws SQLException {
		if (!result.next()) {
			throw new RuntimeException("Could not find " + number + " result for " + type);
		}
	}

	/**
	 * A helper function to get a nullable integer from sql.
	 */
	public Integer getInteger(ResultSet result, String name) throws SQLException {
		Integer integer = result.getInt(name);
		if (result.wasNull()) {
			integer = null;
		}
		return integer;
	}

	/**
	 * A helper function to get a nullable long from sql.
	 */
	public Long getLong(ResultSet result, String name) throws SQLException {
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
	public TimelineInfo getTimelineInfo(PreparedStatement ps) throws SQLException {
		List<EventLink> eventLinks = getList(ps.getResultSet(), this::getEventLink);
		nextResultSet(ps, "tagLinks");
		Set<TagLink> tagLinks = getSet(ps.getResultSet(), this::getTagLink);
		nextResultSet(ps, "personLinks");
		Set<PersonLink> personLinks = getSet(ps.getResultSet(), this::getPersonLink);
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
	public <K extends Idable, V extends Idable> HashMap<K, ArrayList<V>> getRelationsFromRaw(ResultSet resultSet, String keyName, String valueName, Collection<K> keyValues, Collection<V> valueValues) throws SQLException {
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
