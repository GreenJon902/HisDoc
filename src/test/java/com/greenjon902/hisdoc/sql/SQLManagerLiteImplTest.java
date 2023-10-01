package com.greenjon902.hisdoc.sql;

import com.greenjon902.hisdoc.sql.hsqldbImpl.HSQLDBManagerImpl;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SQLManagerLiteImplTest {
	@Test
	public void testGetEvents() throws ClassNotFoundException, SQLException {
		HSQLDBManagerImpl sql = new HSQLDBManagerImpl("jdbc:hsqldb:./test.db;sql.syntax_mys=true");

	}

}