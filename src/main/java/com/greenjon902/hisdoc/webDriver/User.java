package com.greenjon902.hisdoc.webDriver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;

public record User(@NotNull String theme, @NotNull Map<String, String> otherCookies, @Nullable InetSocketAddress address) {
	public User(@NotNull String theme, @NotNull Map<String, String> otherCookies, @Nullable InetSocketAddress address) {
		this.theme = theme;
		this.otherCookies = Collections.unmodifiableMap(otherCookies);
		this.address = address;
	}

	public static User empty() {
		return new User("", Collections.emptyMap(), null);
	}
}
