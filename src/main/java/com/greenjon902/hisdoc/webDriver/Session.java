package com.greenjon902.hisdoc.webDriver;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public record Session(@NotNull String theme, @NotNull Map<String, String> otherCookies) {
	public Session(@NotNull String theme, @NotNull Map<String, String> otherCookies) {
		this.theme = theme;
		this.otherCookies = Collections.unmodifiableMap(otherCookies);
	}

	public static Session empty() {
		return new Session("", Collections.emptyMap());
	}
}
