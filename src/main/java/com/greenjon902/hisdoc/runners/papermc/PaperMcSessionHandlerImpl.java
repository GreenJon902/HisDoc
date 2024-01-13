package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.SessionHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

/**
 * The impl of {@link SessionHandler} for the {@link HisDocRunner PaperMcHisDocRunner}.
 * <br>
 * To create a session, the in-game player must link their account with the website, this is done with these steps:<br>
 * <ol>
 *   <li>The user on the website is given the command from {@link #makeCommand(UUID)}</li>
 *   <li>This is then ran in game which will run {@link #finishLink(String, int)}</li>
 *   <li>Now places can freely run {@link #getPid(UUID)}</li>
 * </ol>
 */
public class PaperMcSessionHandlerImpl implements SessionHandler {
	private final HashMap<UUID, String> pendingLinks = new HashMap<>();
	private final HashMap<UUID, @NotNull Integer> links = new HashMap<>();
	private final Logger logger;

	public PaperMcSessionHandlerImpl(Logger logger) {
		this.logger = logger;
	}

	@Override
	public int getPid(UUID sessionId) {
		if (sessionId == null) return 0;
		int pid = links.getOrDefault(sessionId, 0);
		logger.fine("Pid for " + sessionId + " is " + pid);
		return pid;
	}

	/**
	 * Create the command to be run in game, see {@link PaperMcPermissionHandlerImpl}.
	 */
	public String makeCommand(UUID sessionId) {
		String code;
		if (pendingLinks.containsKey(sessionId)) {  // Same session ID so same code
			code = pendingLinks.get(sessionId);
			logger.fine("Using old code (\"" + code + "\") for sessionId " + sessionId);

		} else { // Generate new code
			do {
				code = RandomStringUtils.random(7, true, true);
			} while (pendingLinks.containsValue(code));  // Ensure no duplicate codes
			logger.fine("Generated code (\"" + code + "\") for sessionId " + sessionId);
			pendingLinks.put(sessionId, code);
		}

		return "/hs link " + code;
	}

	/**
	 * Finish the linking process, see {@link PaperMcPermissionHandlerImpl}.
	 * @return True if the link succeeded
	 */
	public boolean finishLink(String code, int pid) {
		if (pid <= 0) {
			throw new IllegalArgumentException("Pid must be over 0, not " + pid);
		}
		logger.fine("Finishing link for " + code);
		for (Map.Entry<UUID, String> entry : pendingLinks.entrySet()) {
			if (entry.getValue().equals(code)) {
				logger.fine("Who has session id " + entry.getKey());

				// Link succeeded
				pendingLinks.remove(entry.getKey());
				links.put(entry.getKey(), pid);
				return true;
			}
		}
		logger.fine("But failed!");

		return false;
	}
}