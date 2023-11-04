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
import java.util.Set;

import static com.greenjon902.hisdoc.sql.Utils.makeInMemoryConnection;

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
						Collections.emptyList(),
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
						Collections.emptyList(),
						Collections.emptySet()
				),
				eventInfo);
	}

	@Test
	public void should_returnTheEventWithARelation_when_bothEventsExist_and_areRelated() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeUsers").execute();  // Requires this beforehand
		dispatcher.prepare("testGetEvent/makeEvent1").execute();
		dispatcher.prepare("testGetEvent/makeEvent2").execute();
		dispatcher.prepare("testGetEvent/makeEventRelation").execute();


		//waitForNewline();

		// Check both to ensure relation goes both ways

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), "d", 4, "h"),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptyList(),
						Set.of(new EventLink(2, "testing", DateInfo.between(new Timestamp(1500811811000L), new Date(1503442800000L)), "i was testing"))
				),
				eventInfo);


		eventInfo = dispatcher.getEventInfo(2);

		Assertions.assertEquals(
				new EventInfo(2,
						"testing", "i was testing",
						new Timestamp(1696767960000L), new UserLink(1, "User1"),
						DateInfo.between(new Timestamp(1500811811000L), new Date(1503442800000L)),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptyList(),
						Set.of(new EventLink(1, "testing", DateInfo.centered(new Timestamp(1500811811000L), "d", 4, "h"), "i was testing"))
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
						Collections.emptyList(),
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
						List.of(new ChangeInfo(new Timestamp(1500811811000L), new UserLink(2, "User2"), "I did you")),
						Collections.emptySet()
						),
				eventInfo);
	}

}
