package com.greenjon902.hisdoc;

import com.greenjon902.hisdoc.webDriver.User;

import java.util.UUID;

/**
 * Helps the webdriver get the pid of a {@link User}.
 */
public interface SessionHandler {
	/**
	 * Gets a person ID from a session ID
	 * @return The pid, or 0 if no session exists
	 */
	int getPid(UUID sessionId);
}
