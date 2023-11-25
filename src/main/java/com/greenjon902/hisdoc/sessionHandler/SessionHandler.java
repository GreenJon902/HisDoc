package com.greenjon902.hisdoc.sessionHandler;

import com.greenjon902.hisdoc.webDriver.User;

import java.util.Map;

public interface SessionHandler {
	VerifyResult verify(User user, Map<String, String> query);

	String getNameOf(User user);

	/**
	 * Tells the system that a verification can be removed (given it is for example temporary).
	 */
	void suggestConsumeVerification(User user, Map<String, String> query);

	int getPersonId(User user, Map<String, String> query);

	enum VerifyResult {
		NO_SESSION, INVALID_IP, VALID
	}
}
