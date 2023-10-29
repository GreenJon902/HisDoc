package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.webDriver.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestPageBuilder {
	@Test
	public void testEmpty() throws IOException {
		PageBuilder pageBuilder = new PageBuilder();
		String rendered = pageBuilder.render(Session.empty());

		Assertions.assertFalse(rendered.contains("title"));
	}

	@Test
	public void testEmptyTitled() throws IOException {
		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("Empty Page");
		String rendered = pageBuilder.render(Session.empty());

		Assertions.assertTrue(rendered.contains("title"));
		Assertions.assertTrue(rendered.contains("Empty Page"));
	}
}
