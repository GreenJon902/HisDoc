package com.greenjon902.hisdoc.sessionHandler;

import com.greenjon902.hisdoc.webDriver.User;

public interface SessionHandler {
	VerifyResult verify(User user);

	String getNameOf(User user);

	enum VerifyResult {
		NO_SESSION, INVALID_IP, VALID
	}
}
