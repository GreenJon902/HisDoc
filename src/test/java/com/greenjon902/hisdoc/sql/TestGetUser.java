package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.greenjon902.hisdoc.sql.Utils.makeInMemoryConnection;

public class TestGetUser {
	@Test
	public void should_returnNoUser_when_noUsersExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		UserInfo userInfo = dispatcher.getUserInfo(1);

		Assertions.assertNull(userInfo);
	}

	@Test
	public void should_returnNoUser_when_thatUserDoesNotExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetUser/makeUser1").execute();

		UserInfo userInfo = dispatcher.getUserInfo(2);

		Assertions.assertNull(userInfo);
	}

	@Test
	public void should_returnTheEvent_when_usingUser1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetUser/makeUser1").execute();


		//waitForNewline();

		UserInfo userInfo = dispatcher.getUserInfo(1);

		Assertions.assertEquals(
				new UserInfo(1, "User1", Collections.emptyMap(), 0, 0, Collections.emptyList()),
				userInfo);
	}

	@Test
	public void should_returnTheEvent_when_usingUser2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetUser/makeUser2").execute();

		UserInfo userInfo = dispatcher.getUserInfo(2);

		//waitForNewline();

		Assertions.assertEquals(
				new UserInfo(2, "User2",
						Map.of(
							new TagLink(1, "Tag1", 123), 2,
							new TagLink(2, "Tag2", 321), 1
						), 2, 2, List.of(
								new EventLink(1, "testing1"),
								new EventLink(2, "testing2")
				)),
				userInfo);
	}

	@Test
	public void should_returnTheCorrectEvent_when_usingUser1AndUser2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetUser/makeUser1").execute();
		dispatcher.prepare("testGetUser/makeUser2").execute();

		UserInfo userInfo = dispatcher.getUserInfo(1);
		Assertions.assertEquals(
				new UserInfo(1, "User1", Collections.emptyMap(), 0, 0, Collections.emptyList()),
				userInfo);

		userInfo = dispatcher.getUserInfo(2);
		Assertions.assertEquals(
				new UserInfo(2, "User2",
						Map.of(
								new TagLink(1, "Tag1", 123), 2,
								new TagLink(2, "Tag2", 321), 1
						), 2, 2, List.of(
						new EventLink(1, "testing1"),
						new EventLink(2, "testing2")
				)),
				userInfo);
	}
}
