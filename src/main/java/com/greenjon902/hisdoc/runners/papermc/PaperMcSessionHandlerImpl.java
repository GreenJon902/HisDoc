package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

import static com.greenjon902.hisdoc.SessionHandler.VerifyResult.*;

/**
 * The impl of {@link SessionHandler} for the PaperMc{@link HisDocRunner}.
 * Note, this only allows one verification per personId.
 */
public class PaperMcSessionHandlerImpl implements SessionHandler {
	private final HashMap<String, @Nullable String> codeToExpectedIpMap = new HashMap<>();  // If no record of a code
																		// exists, assume we don't need to check the ip
	private final HashMap<String, String> codeToNameMap = new HashMap<>();
	private final HashMap<String, Integer> codeToPidMap = new HashMap<>();
	private final HashSet<String> persistingCodes = new HashSet<>();
	private final Logger logger;
	private final String addEventUrl;

	public PaperMcSessionHandlerImpl(Logger logger, String addEventUrl) {
		this.logger = logger;
		this.addEventUrl = addEventUrl;
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
	public boolean suggestConsumeVerification(User user, Map<String, String> query) {
		String code = query.get("code");

		if (persistingCodes.contains(code)) {
			logger.finer("Code " + code + " is marked to persist so not consuming");
			return false;

		} else {
			logger.finer("Removing verification for user " + user.address() + " with code " + code);
			if (code == null) {
				throw new IllegalStateException("Tried to remove verification for user without code - User " + user + " with query " + query);
			}
			codeToExpectedIpMap.remove(code);
			codeToNameMap.remove(code);
			codeToPidMap.remove(code);
			return true;
		}
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

	/**
	 * Gets the code from a personId if it exists.
	 * @param pid The pid to search for
	 * @return The code or null if no person with that pid is verified
	 */
	private @Nullable String getCodeFromPid(int pid) {
		String code = null;
		for (Map.Entry<String, Integer> entry : codeToPidMap.entrySet()) {
			if (entry.getValue() == pid) {
				code = entry.getKey();
				break;
			}
		}
		return code;
	}

	@Override
	public String getPostUrl(int personId) {
		logger.finer("Getting url for " + personId);

		String code = getCodeFromPid(personId);
		if (code == null) {
			throw new IllegalStateException("Person with pid " + personId + " has no verifications");
		}

		String url = addEventUrl + "?code=" + code;
		logger.fine("Url for person " + personId + " is \"" + url + "\"");
		return url;
	}

	/**
	 * Adds a verification with the given information
	 * @param code The code to use
	 * @param pid The pid of the person posting the event
	 * @param name The ingame name of the person posting the event
	 * @param ip The ip of the person posting the event
	 * @param persist Should the verification persist
	 * @return Whether a previous verification was replaced for the given pid
	 * @throws IllegalStateException If a verification with that code already exists
	 */
	public boolean addVerification(String code, int pid, String name, String ip, boolean persist) {
		logger.finer("Adding verification code=\"" + code + "\", pid=\"" + pid + "\", name=\"" + name + "\", " +
				"ip=\"" + ip + "\", persist=" + persist);
		if (codeToPidMap.containsKey(code)) throw new IllegalStateException("Code \"" + code + "\" is already allocated!");

		boolean removedAny = false;
		while (true) {  // Just in case check for multiple occurrences of the pid and remove them all
			String previousCode = getCodeFromPid(pid);

			if (previousCode == null) {
				break;
			}

			logger.finer("A previous verification with code " + code + " was found, removing");

			codeToExpectedIpMap.remove(previousCode);
			codeToPidMap.remove(previousCode);
			codeToNameMap.remove(previousCode);
			persistingCodes.remove(previousCode);
			removedAny = true;
		}

		codeToExpectedIpMap.put(code, ip);
		codeToPidMap.put(code, pid);
		codeToNameMap.put(code, name);
		if (persist) persistingCodes.add(code);

		return removedAny;
	}
}