package com.greenjon902.hisdoc.webDriver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @param pid Set to 0 for null / unknown
 */
public record User(@NotNull String theme, @Nullable UUID sessionId, int pid, @NotNull Map<String, String> otherCookies, @Nullable InetSocketAddress address,
				   Map<String, String> post) {
	public User(@NotNull String theme, @Nullable UUID sessionId, int pid, @NotNull Map<String, String> otherCookies, @Nullable InetSocketAddress address, @NotNull Map<String, String> post) {
		this.theme = theme;
		this.otherCookies = Collections.unmodifiableMap(otherCookies);
		this.address = address;
		this.post = Collections.unmodifiableMap(post);
		this.sessionId = sessionId;
		this.pid = pid;

		if (pid < 0) throw new IllegalArgumentException("PID must be above 0, not " + pid);
	}

	public static User empty() {
		return new User("", null, 0, Collections.emptyMap(), null, Collections.emptyMap());
	}
}
