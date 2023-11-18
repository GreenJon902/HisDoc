package com.greenjon902.hisdoc.sessionHandler.impl.testSessionHandlerImpl;

import com.greenjon902.hisdoc.sessionHandler.SessionHandler;
import com.greenjon902.hisdoc.webDriver.User;

public class TestSessionHandlerImpl implements SessionHandler {
	private final VerifyResult verifyResult;

	public TestSessionHandlerImpl(VerifyResult verifyResult) {
		this.verifyResult = verifyResult;
	}

	@Override
	public VerifyResult verify(User user) {
		return verifyResult;
	}

	@Override
	public String getNameOf(User user) {
		return "TestUserName";
	}
}
