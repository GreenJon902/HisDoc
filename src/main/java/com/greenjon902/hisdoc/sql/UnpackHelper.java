package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

public class UnpackHelper {
	/**
	 * Unpacks a singular {@link UserData} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns userType, and userData.
	 */
	public static UserData getUserData(ResultSet result) throws SQLException {
		return new UserData(result.getString("userType"), result.getString("userData"));
	}

	/**
	 * Unpacks a singular {@link ChangeInfo} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns date, authorUid, authorInfo, and description.
	 */
	public static ChangeInfo getChangeInfo(ResultSet result) throws SQLException {
		return new ChangeInfo(result.getTimestamp("date"),
				new UserLink(result.getInt("authorUid"), getUserData(result)),
				result.getString("description"));
	}

	/**
	 * Unpacks a singular {@link DateInfo} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eventDateType, eventDate1, eventDatePrecision, eventDateDiff, eventDateDiffType and
	 * eventDate2.
	 */
	public static DateInfo getDateInfo(ResultSet result) throws SQLException {
		return new DateInfo(
				result.getString("eventDateType"),
				result.getTimestamp("eventDate1"),
				result.getString("eventDatePrecision"),
				getInteger(result, "eventDateDiff"),
				result.getString("eventDateDiffType"),
				result.getDate("eventDate2")
		);
	}

	/**
	 * Unpacks a singular {@link EventLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns eid, name.
	 */
	public static EventLink getEventLink(ResultSet result) throws SQLException {
		return new EventLink(result.getInt("eid"), result.getString("name"), getDateInfo(result),
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
	 * Unpacks a singular {@link UserLink} from a {@link ResultSet}, this expects the current result to be the one we are
	 * getting (meaning we do not use {@link ResultSet#next()}).
	 * This looks at the columns uid, userInfo.
	 */
	public static UserLink getUserLink(ResultSet result) throws SQLException {
		return new UserLink(result.getInt("uid"), getUserData(result));
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
	public static EventInfo getEventInfo(PreparedStatement ps, int eid) throws SQLException {
		Set<TagLink> tagLinks = getSet(ps.getResultSet(), UnpackHelper::getTagLink);
		nextResultSet(ps, "userLinks");
		Set<UserLink> userLinks = getSet(ps.getResultSet(), UnpackHelper::getUserLink);
		nextResultSet(ps, "eventLinks");
		Set<EventLink> eventLinks = getSet(ps.getResultSet(), UnpackHelper::getEventLink);
		nextResultSet(ps, "changeInfos");
		List<ChangeInfo> changeInfos = UnpackHelper.getList(ps.getResultSet(), UnpackHelper::getChangeInfo);

		nextResultSet(ps, "event");
		ResultSet result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}
		DateInfo eventDateInfo = getDateInfo(result);

		Integer postedUid;
		UserLink postedUser = null;
		if ((postedUid = getInteger(result, "postedUid")) != null) {
			postedUser = new UserLink(postedUid, getUserData(result));
		}

		return new EventInfo(
				eid,
				result.getString("name"),
				result.getString("description"),
				result.getTimestamp("postedDate"),
				postedUser,
				eventDateInfo,
				tagLinks,
				userLinks,
				changeInfos,
				eventLinks,
				result.getString("details")
		);
	}

	/**
	 * Unpacks a single {@link UserLink} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public static UserInfo getUserInfo(PreparedStatement ps, int uid) throws SQLException {
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

		nextResultSet(ps, "user");
		result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}

		return new UserInfo(
				uid,
				getUserData(result),
				countedTagLinks,
				postCount, eventCount, recentEvents, recentPosts);
	}

	/**
	 * Unpacks a single {@link TagInfo} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public static TagInfo getTagInfo(PreparedStatement ps, int tid) throws SQLException {
		List<EventLink> recentEventLinks = getList(ps.getResultSet(), UnpackHelper::getEventLink);
		nextResultSet(ps, "tagInfo");
		ResultSet result = ps.getResultSet();
		if (!result.next()) {
			return null;
		}

		return new TagInfo(
				tid,
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
	 * Unpacks multiple {@link EventInfo} from a {@link PreparedStatement}, this expects the current result set to be the
	 * first item to be unpacked, this also expects the next result to be the one we are
	 * getting (meaning we run {@link ResultSet#next()} before doing any unpacking).
	 */
	public static TimelineInfo getTimelineInfo(PreparedStatement ps) throws SQLException {
		List<EventLink> eventLinks = getList(ps.getResultSet(), UnpackHelper::getEventLink);

		return new TimelineInfo(eventLinks);
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
