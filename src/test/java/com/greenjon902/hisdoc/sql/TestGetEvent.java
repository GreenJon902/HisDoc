package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.results.ChangeInfo;
import com.greenjon902.hisdoc.sql.results.DateInfo;
import com.greenjon902.hisdoc.sql.results.EventInfo;
import com.greenjon902.hisdoc.sql.results.TagInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.sql.*;
import java.util.Set;

import static com.greenjon902.hisdoc.sql.Utils.*;

public class TestGetEvent {
	@Test
	public void should_returnNoEvents_when_noEventsExist() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		EventInfo eventInfo = dispatcher.getEventInfo(0);

		Assertions.assertNull(eventInfo);
	}

	@Test
	public void should_returnTheEvents_when_onlyThatEventExists_and_usingDateFrom0() throws SQLException {
		Connection conn = makeInMemoryConnection();
		Dispatcher dispatcher = new Dispatcher(conn);
		dispatcher.createTables();

		dispatcher.prepare("testGetEvent/makeEvent0").execute();


		//waitForNewline();

		EventInfo eventInfo = dispatcher.getEventInfo(1);

		Assertions.assertEquals(
				new EventInfo(1,
						"testing", "i was testing",
						DateInfo.centered(new Timestamp(2017, 7, 23, 13, 10, 11, 0), "d", 4, "h"),
						new TagInfo[]{},
						new String[]{},
						new ChangeInfo[]{}
						),
				eventInfo);
	}

}
