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
	 * @return Whether the verification was consumed
	 */
	boolean suggestConsumeVerification(User user, Map<String, String> query);

	int getPersonId(User user, Map<String, String> query);

	/**
	 * Gets the url to post an event. This takes a pid and returns a url, this may be the same or different for
	 * different people depending on the implementation.
	 * @throws IllegalStateException if the person given can't add an event
	 * @param personId The person id who is posting the event
	 * @return The url to use
	 */
	String getPostUrl(int personId);

	enum VerifyResult {
		NO_SESSION, INVALID_IP, VALID
	}
}
