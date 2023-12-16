package com.greenjon902.hisdoc;

import com.greenjon902.hisdoc.webDriver.User;

import java.util.Map;

public interface SessionHandler {
	VerifyResult verify(User user, Map<String, String> query);

	/**
	 * Gets the name of this user. This expects that you have already verified that they are supposed to be here.
	 * @throws IllegalStateException When the user is not verified
	 */
	String getNameOf(User user, Map<String, String> query);

	/**
	 * Tells the system that a verification can be removed (given it is for example temporary).
	 */
	void suggestConsumeVerification(User user, Map<String, String> query);

	int getPersonId(User user, Map<String, String> query);

	enum VerifyResult {
		NO_SESSION, INVALID_IP, VALID
	}
}
