package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.sqlLiteImpl.SQLLiteManagerImpl;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SQLManagerLiteImplTest {
	@Test
	public void testGetEvents() throws ClassNotFoundException, SQLException {
		SQLLiteManagerImpl sql = new SQLLiteManagerImpl("jdbc:sqlite:./test.db");

	}

}