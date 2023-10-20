package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.pageBuilder.widgets.LogoBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestSimpleWidgets {
	@Test
	public void testLogoBuilder() throws IOException {
		PageBuilder pageBuilder = new PageBuilder();
		LogoBuilder logoBuilder = new LogoBuilder();
		pageBuilder.add(logoBuilder);

		pageBuilder.render();
	}
}
