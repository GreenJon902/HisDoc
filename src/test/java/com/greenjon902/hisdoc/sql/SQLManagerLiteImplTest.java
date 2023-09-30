package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.sqlLiteImpl.SQLLiteImpl;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SQLLiteImplTest {
	@Test
	public void testGetEvents() throws ClassNotFoundException, SQLException {
		SQLLiteImpl sql = new SQLLiteImpl("jdbc:sqlite:./test.db");

	}

}