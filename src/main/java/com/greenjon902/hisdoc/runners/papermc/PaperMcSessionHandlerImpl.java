package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.greenjon902.hisdoc.SessionHandler.VerifyResult.*;

public class PaperMcSessionHandlerImpl implements SessionHandler {
	private final HashMap<String, @Nullable String> codeToExpectedIpMap = new HashMap<>();  // If no record of a code
																		// exists, assume we don't need to check the ip
	private final HashMap<String, String> codeToNameMap = new HashMap<>();
	private final HashMap<String, Integer> codeToPidMap = new HashMap<>();
	private final Logger logger;

	public PaperMcSessionHandlerImpl(Logger logger) {
		this.logger = logger;
	}

	@Override
	public VerifyResult verify(User user, Map<String, String> query) {
		String code = query.get("code");
		logger.finer("Verifying user " + user.address() + " with code " + code);
		if (code == null) {
			logger.finer("No session due to no code!");
			return NO_SESSION;
		}
		if (!codeToPidMap.containsKey(code)) { // Can't check using IP map as that may not have a record for all verifications
			logger.finer("No session due to code being invalid");
			return NO_SESSION;
		}

		String expectedIp = codeToExpectedIpMap.get(code);
		logger.finer("Expected IP: \"" + expectedIp + "\", Got IP: \"" + (user.address() != null ? user.address().getHostString() : null));
		if (expectedIp != null && user.address() != null && !Objects.equals(user.address().getHostString(), expectedIp)) {
			logger.finest("So invalid ip!");
			return INVALID_IP;
		}

		return VALID;
	}

	@Override
	public String getNameOf(User user, Map<String, String> query) {
		String code = query.get("code");
		logger.finest("Getting name of user " + user.address() + " with code " + code);
		if (code == null) {
			throw new IllegalStateException("User " + user + " with query " + query + " has no code given!");
		}
		String name = codeToNameMap.get(code);
		if (name == null) {
			throw new IllegalStateException("I don't know the name of the user " + user + " with query " + query);
		}
		logger.finest("Got name " + name);
		return name;
	}

	@Override
	public void suggestConsumeVerification(User user, Map<String, String> query) {
		String code = query.get("code");
		logger.finer("Removing verification for user " + user.address() + " with code " + code);
		if (code == null) {
			throw new IllegalStateException("Tried to remove verification for user without code - User " + user + " with query " + query);
		}
		codeToExpectedIpMap.remove(code);
		codeToNameMap.remove(code);
		codeToPidMap.remove(code);
	}

	@Override
	public int getPersonId(User user, Map<String, String> query) {
		String code = query.get("code");
		logger.finest("Getting pid of user " + user.address() + " with code " + code);
		if (code == null) {
			throw new IllegalStateException("User " + user + " with query " + query + " has no code given!");
		}
		Integer pid = codeToPidMap.get(code);
		logger.finest("Got " + pid);
		if (pid == null) {
			throw new IllegalStateException("I don't know the person id of the user " + user + " with query " + query);
		}
		return pid;
	}

	public void addVerification(String code, Integer pid, String name, String ip) {
		logger.finer("Adding verification code=\"" + code + "\", pid=\"" + pid + "\", name=\"" + name + "\", ip=\"" + ip + "\"");
		if (codeToPidMap.containsKey(code)) throw new IllegalStateException("Code \"" + code + "\" is already allocated!");

		codeToExpectedIpMap.put(code, ip);
		codeToPidMap.put(code, pid);
		codeToNameMap.put(code, name);
	}
}
