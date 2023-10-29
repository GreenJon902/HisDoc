package com.greenjon902.hisdoc.webDriver;

import org.jetbrains.annotations.NotNull;

public record Session(@NotNull String theme) {
	public static Session empty() {
		return new Session("");
	}
}
