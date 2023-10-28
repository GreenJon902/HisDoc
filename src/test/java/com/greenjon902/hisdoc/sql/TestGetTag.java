package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.greenjon902.hisdoc.sql.Utils.makeInMemoryConnection;

public class TestGetTag {
	@Test
	public void should_returnNoTag_when_noTagsExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		TagInfo tagInfo = dispatcher.getTagInfo(1);

		Assertions.assertNull(tagInfo);
	}

	@Test
	public void should_returnNoTag_when_thatTagDoesNotExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetTag/makeTag1").execute();

		TagInfo tagInfo = dispatcher.getTagInfo(2);

		Assertions.assertNull(tagInfo);
	}

	@Test
	public void should_returnTheTag_when_usingTag1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetTag/makeTag1").execute();


		//waitForNewline();

		TagInfo tagInfo = dispatcher.getTagInfo(1);

		Assertions.assertEquals(
				new TagInfo(1, "Tag1", "This is a blue tag if i know how the rgb works", 255, Collections.emptyList()),
				tagInfo);
	}

	@Test
	public void should_returnTheTag_when_usingTag2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetTag/makeTag2").execute();

		TagInfo tagInfo = dispatcher.getTagInfo(2);

		//waitForNewline();

		Assertions.assertEquals(
				new TagInfo(2, "Tag2", "This is another tag with a color that i dont quite know", 123,
						List.of(
								new EventLink(4, "testing4", DateInfo.centered(Timestamp.valueOf("2017-07-24 13:10:11"), "d", 4, "h")),
								new EventLink(1, "testing1", DateInfo.centered(Timestamp.valueOf("2012-07-24 13:10:11"), "d", 4, "h")),
								new EventLink(2, "testing2", DateInfo.between(Timestamp.valueOf("2010-07-23 13:10:11"), Date.valueOf("2017-08-23")))
						)),
				tagInfo);
	}

	@Test
	public void should_returnTheCorrectTag_when_usingTag1AndTag2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetTag/makeTag1").execute();
		dispatcher.prepare("testGetTag/makeTag2").execute();

		TagInfo tagInfo = dispatcher.getTagInfo(1);
		Assertions.assertEquals(
				new TagInfo(1, "Tag1", "This is a blue tag if i know how the rgb works", 255, Collections.emptyList()),
				tagInfo);

		tagInfo = dispatcher.getTagInfo(2);
		Assertions.assertEquals(
				new TagInfo(2, "Tag2", "This is another tag with a color that i dont quite know", 123,
						List.of(
								new EventLink(4, "testing4", DateInfo.centered(Timestamp.valueOf("2017-07-24 13:10:11"), "d", 4, "h")),
								new EventLink(1, "testing1", DateInfo.centered(Timestamp.valueOf("2012-07-24 13:10:11"), "d", 4, "h")),
								new EventLink(2, "testing2", DateInfo.between(Timestamp.valueOf("2010-07-23 13:10:11"), Date.valueOf("2017-08-23")))
						)),
				tagInfo);
	}
}
