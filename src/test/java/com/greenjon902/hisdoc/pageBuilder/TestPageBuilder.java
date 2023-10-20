package com.greenjon902.hisdoc.pageBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestPageBuilder {
	@Test
	public void testEmpty() throws IOException {
		PageBuilder pageBuilder = new PageBuilder();
		String rendered = pageBuilder.render();

		Assertions.assertFalse(rendered.contains("title"));
	}

	@Test
	public void testEmptyTitled() throws IOException {
		PageBuilder pageBuilder = new PageBuilder();
		pageBuilder.title("Empty Page");
		String rendered = pageBuilder.render();

		Assertions.assertFalse(rendered.contains("title"));
		Assertions.assertFalse(rendered.contains("Empty Page"));
	}
}
