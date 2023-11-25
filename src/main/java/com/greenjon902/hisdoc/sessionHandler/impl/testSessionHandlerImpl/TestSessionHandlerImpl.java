package com.greenjon902.hisdoc.sessionHandler.impl.testSessionHandlerImpl;

import com.greenjon902.hisdoc.sessionHandler.SessionHandler;
import com.greenjon902.hisdoc.webDriver.User;

import java.util.Map;

public class TestSessionHandlerImpl implements SessionHandler {
	private final VerifyResult verifyResult;

	public TestSessionHandlerImpl(VerifyResult verifyResult) {
		this.verifyResult = verifyResult;
	}

	@Override
	public VerifyResult verify(User user, Map<String, String> query) {
		return verifyResult;
	}

	@Override
	public String getNameOf(User user) {
		return "TestUserName";
	}

	@Override
	public void suggestConsumeVerification(User user, Map<String, String> query) {
		if (verifyResult != VerifyResult.VALID) {
			throw new RuntimeException("Cannot unverify user " + user + " as is not verified in the first place");
		}
	}

	@Override
	public int getPersonId(User user, Map<String, String> query) {
		if (verifyResult != VerifyResult.VALID) {
			throw new RuntimeException("Cannot get user " + user + " as is not verified");
		}
		return 1;
	}
}
