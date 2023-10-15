package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Collections;
import java.util.Set;

import static com.greenjon902.hisdoc.sql.Utils.*;

public class TestGetEvent {
	@Test
	public void should_returnNoEvent_when_noEventsExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertNull(eventInfo);
	}

	@Test
	public void should_returnNoEvent_when_thatEventDoesNotExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();

		EventInfo eventInfo = dispatcher.getEventInfo(2);

		Assertions.assertNull(eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyThatEventExists_and_usingEvent1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), "d", 4, "h"),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptySet()
						),
				eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyThatEventExists_and_usingEvent2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeUsers").execute();  // Requires this beforehand
		dispatcher.prepare("testGetEvent/makeEvent2").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(2);

		Assertions.assertEquals(
				new EventInfo(2,
						"testing", "i was testing",
						new Timestamp(1696767960000L), new UserLink(1, "User1"),
						DateInfo.between(new Timestamp(1500811811000L), new Date(1503442800000L)),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptySet()
				),
				eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyOneEventExists_andOtherDataPiecesExistButAreNotRelated_and_usingEvent1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();
		dispatcher.prepare("testGetEvent/makeTags").execute();
		dispatcher.prepare("testGetEvent/makeUsers").execute();
		dispatcher.prepare("testGetEvent/makeChangeLogsFor5And6").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), "d", 4, "h"),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptySet()
						),
				eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyOneEventExists_andOtherDataPiecesExistAndAreRelated_and_usingEvent1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();
		dispatcher.prepare("testGetEvent/makeTags").execute();
		dispatcher.prepare("testGetEvent/makeTagRelations").execute();
		dispatcher.prepare("testGetEvent/makeUsers").execute();
		dispatcher.prepare("testGetEvent/makeUserRelations").execute();
		dispatcher.prepare("testGetEvent/makeChangeLogsFor1And2").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), "d", 4, "h"),
						Set.of(new TagLink(2, "Tag2", 321)),
						Set.of(new UserLink(2, "User2")),
						Set.of(new ChangeInfo(new Timestamp(1500811811000L), new UserLink(2, "User2"), "I did you"))
						),
				eventInfo);
	}

}
