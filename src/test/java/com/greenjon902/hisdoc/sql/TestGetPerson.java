package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.greenjon902.hisdoc.sql.Utils.getTestLogger;
import static com.greenjon902.hisdoc.sql.Utils.makeInMemoryConnection;
import static com.greenjon902.hisdoc.sql.results.DateInfo.Precision.DAY;
import static com.greenjon902.hisdoc.sql.results.DateInfo.Precision.HOUR;

public class TestGetPerson {
	@Test
	public void should_returnNoPerson_when_noPersonsExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		PersonInfo personInfo = dispatcher.getPersonInfo(1);

		Assertions.assertNull(personInfo);
	}

	@Test
	public void should_returnNoPerson_when_thatPersonDoesNotExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetPerson/makePerson1").execute();

		PersonInfo personInfo = dispatcher.getPersonInfo(2);

		Assertions.assertNull(personInfo);
	}

	@Test
	public void should_returnThePerson_when_usingPerson1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetPerson/makePerson1").execute();


		//waitForNewline();

		PersonInfo personInfo = dispatcher.getPersonInfo(1);

		Assertions.assertEquals(
				new PersonInfo(1, PersonData.miscellaneous("Person1"), Collections.emptyMap(), 0, 0, Collections.emptyList(), Collections.emptyList()),
				personInfo);
	}

	@Test
	public void should_returnThePerson_when_usingPerson2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetPerson/makePerson2").execute();

		PersonInfo personInfo = dispatcher.getPersonInfo(2);

		//waitForNewline();

		Assertions.assertEquals(
				new PersonInfo(2, PersonData.miscellaneous("Person2"),
						Map.of(
							new TagLink(1, "Tag1", 123), 2,
							new TagLink(2, "Tag2", 321), 1
						), 2, 2, List.of(
						new EventLink(1, "testing1", DateInfo.centered(Timestamp.valueOf("2017-07-24 13:10:11"), DAY, 4, HOUR), "i was testing"),
						new EventLink(2, "testing2", DateInfo.between(Timestamp.valueOf("2017-07-23 13:10:11"), Date.valueOf("2017-08-23")), "i was testing again")
				), List.of(
						new EventLink(3, "testing3", DateInfo.between(Timestamp.valueOf("2017-07-25 13:10:11"), Date.valueOf("2017-08-23")), "more testing"),
						new EventLink(2, "testing2", DateInfo.between(Timestamp.valueOf("2017-07-23 13:10:11"), Date.valueOf("2017-08-23")), "i was testing again")
				)),
				personInfo);
	}

	@Test
	public void should_returnTheCorrectPerson_when_usingPerson1AndPerson2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetPerson/makePerson1").execute();
		dispatcher.prepare("testGetPerson/makePerson2").execute();

		PersonInfo personInfo = dispatcher.getPersonInfo(1);
		Assertions.assertEquals(
				new PersonInfo(1, PersonData.miscellaneous("Person1"), Collections.emptyMap(), 0, 0, Collections.emptyList(), Collections.emptyList()),
				personInfo);

		personInfo = dispatcher.getPersonInfo(2);
		Assertions.assertEquals(
				new PersonInfo(2, PersonData.miscellaneous("Person2"),
						Map.of(
								new TagLink(1, "Tag1", 123), 2,
								new TagLink(2, "Tag2", 321), 1
						), 2, 2, List.of(
						new EventLink(1, "testing1", DateInfo.centered(Timestamp.valueOf("2017-07-24 13:10:11"), DAY, 4, HOUR), "i was testing"),
						new EventLink(2, "testing2", DateInfo.between(Timestamp.valueOf("2017-07-23 13:10:11"), Date.valueOf("2017-08-23")), "i was testing again")
				), List.of(
						new EventLink(3, "testing3", DateInfo.between(Timestamp.valueOf("2017-07-25 13:10:11"), Date.valueOf("2017-08-23")), "more testing"),
						new EventLink(2, "testing2", DateInfo.between(Timestamp.valueOf("2017-07-23 13:10:11"), Date.valueOf("2017-08-23")), "i was testing again")
				)),
				personInfo);
	}
}
