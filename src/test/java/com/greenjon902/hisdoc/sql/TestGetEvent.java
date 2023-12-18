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

import static com.greenjon902.hisdoc.Utils.getTestLogger;
import static com.greenjon902.hisdoc.sql.Utils.makeInMemoryConnection;
import static com.greenjon902.hisdoc.sql.results.DateInfo.Precision.DAY;
import static com.greenjon902.hisdoc.sql.results.DateInfo.Precision.HOUR;

public class TestGetEvent {
	@Test
	public void should_returnNoEvent_when_noEventsExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertNull(eventInfo);
	}

	@Test
	public void should_returnNoEvent_when_thatEventDoesNotExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();

		EventInfo eventInfo = dispatcher.getEventInfo(2);

		Assertions.assertNull(eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyThatEventExists_and_usingEvent1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), DAY, 4, HOUR),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptyList(),
						Collections.emptySet(),
						null
						),
				eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyThatEventExists_and_usingEvent2() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makePersons").execute();  // Requires this beforehand
		dispatcher.prepare("testGetEvent/makeEvent2").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(2);

		Assertions.assertEquals(
				new EventInfo(2,
						"testing2", "i was testing",
						new Timestamp(1696767960000L), new PersonLink(1, PersonData.miscellaneous("Person1")),
						DateInfo.between(new Timestamp(1500811811000L), new Date(1503442800000L)),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptyList(),
						Collections.emptySet(),
						null
				),
				eventInfo);
	}

	@Test
	public void should_returnTheEventWithARelation_when_bothEventsExist_and_areRelated() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makePersons").execute();  // Requires this beforehand
		dispatcher.prepare("testGetEvent/makeEvent1").execute();
		dispatcher.prepare("testGetEvent/makeEvent2").execute();
		dispatcher.prepare("testGetEvent/makeEventRelation").execute();


		//waitForNewline();

		// Check both to ensure relation goes both ways

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), DAY, 4, HOUR),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptyList(),
						Set.of(new EventLink(2, "testing2", DateInfo.between(new Timestamp(1500811811000L), new Date(1503442800000L)), "i was testing")),
						null
				),
				eventInfo);


		eventInfo = dispatcher.getEventInfo(2);

		Assertions.assertEquals(
				new EventInfo(2,
						"testing2", "i was testing",
						new Timestamp(1696767960000L), new PersonLink(1, PersonData.miscellaneous("Person1")),
						DateInfo.between(new Timestamp(1500811811000L), new Date(1503442800000L)),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptyList(),
						Set.of(new EventLink(1, "testing", DateInfo.centered(new Timestamp(1500811811000L), DAY, 4, HOUR), "i was testing")),
						null
				),
				eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyOneEventExists_andOtherDataPiecesExistButAreNotRelated_and_usingEvent1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();
		dispatcher.prepare("testGetEvent/makeTags").execute();
		dispatcher.prepare("testGetEvent/makePersons").execute();
		dispatcher.prepare("testGetEvent/makeChangeLogsFor5And6").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), DAY, 4, HOUR),
						Collections.emptySet(),
						Collections.emptySet(),
						Collections.emptyList(),
						Collections.emptySet(),
						null
						),
				eventInfo);
	}

	@Test
	public void should_returnTheEvent_when_onlyOneEventExists_andOtherDataPiecesExistAndAreRelated_and_usingEvent1() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn, getTestLogger());
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent1").execute();
		dispatcher.prepare("testGetEvent/makeTags").execute();
		dispatcher.prepare("testGetEvent/makeTagRelations").execute();
		dispatcher.prepare("testGetEvent/makePersons").execute();
		dispatcher.prepare("testGetEvent/makePersonRelations").execute();
		dispatcher.prepare("testGetEvent/makeChangeLogsFor1And2").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(1500811811000L), DAY, 4, HOUR),
						Set.of(new TagLink(2, "Tag2", 321)),
						Set.of(new PersonLink(2, PersonData.miscellaneous("Person2"))),
						List.of(new ChangeInfo(new Timestamp(1500811811000L), new PersonLink(2, PersonData.miscellaneous("Person2")), "I did you")),
						Collections.emptySet(),
						null
						),
				eventInfo);
	}

}
