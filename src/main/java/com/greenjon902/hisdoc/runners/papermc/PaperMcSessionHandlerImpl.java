package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.SessionHandler;
import com.greenjon902.hisdoc.webDriver.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.greenjon902.hisdoc.SessionHandler.VerifyResult.*;

public class PaperMcSessionHandlerImpl implements SessionHandler {
	private final HashMap<String, @Nullable String> codeToExpectedIpMap = new HashMap<>();  // If no record of a code
																		// exists, assume we don't need to check the ip
	private final HashMap<String, String> codeToNameMap = new HashMap<>();
	private final HashMap<String, Integer> codeToPidMap = new HashMap<>();

	@Override
	public VerifyResult verify(User user, Map<String, String> query) {
		System.out.println("\n\nVerifiying user");
		String code = query.get("code");
		System.out.println(code);
		System.out.println(codeToPidMap);
		System.out.println(codeToExpectedIpMap);
		System.out.println(codeToNameMap);
		if (code == null) return NO_SESSION;
		System.out.println(codeToPidMap.get(code));
		if (!codeToPidMap.containsKey(code)) return NO_SESSION;  // Can't check using IPs as that may be null;

		String expectedIp = codeToExpectedIpMap.get(code);
		if (expectedIp != null && user.address() != null && !Objects.equals(user.address().getHostString(), expectedIp))
			return INVALID_IP;

		return VALID;
	}

	@Override
	public String getNameOf(User user, Map<String, String> query) {
		String code = query.get("code");
		if (code == null) {
			throw new IllegalStateException("User " + user + " with query " + query + " has no code given!");
		}
		String name = codeToNameMap.get(code);
		if (name == null) {
			throw new IllegalStateException("I don't know the name of the user " + user + " with query " + query);
		}
		return name;
	}

	@Override
	public void suggestConsumeVerification(User user, Map<String, String> query) {
		String code = query.get("code");
		System.out.println("Removing " + code);
		if (code == null) {
			throw new IllegalStateException("Tried to remove verification for user without code - User " + user + " with query " + query);
		}
		codeToExpectedIpMap.remove(code);
		codeToNameMap.remove(code);
		codeToPidMap.remove(code);
	}

	@Override
	public int getPersonId(User user, Map<String, String> query) {
		System.out.println("Getting person id for " + query);
		String code = query.get("code");
		System.out.println(code);
		if (code == null) {
			throw new IllegalStateException("User " + user + " with query " + query + " has no code given!");
		}
		Integer pid = codeToPidMap.get(code);
		System.out.println(pid);
		if (pid == null) {
			throw new IllegalStateException("I don't know the person id of the user " + user + " with query " + query);
		}
		return pid;
	}

	public void addVerification(String code, Integer pid, String name, String ip) {
		codeToExpectedIpMap.put(code, ip);
		codeToPidMap.put(code, pid);
		codeToNameMap.put(code, name);
	}
}
