package com.greenjon902.hisdoc.person;

import de.saibotk.jmaw.ApiResponseException;
import de.saibotk.jmaw.MojangAPI;
import de.saibotk.jmaw.PlayerProfile;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public record MinecraftPerson(UUID uuid) implements Person {
	private static final MojangAPI mojangAPI = new MojangAPI();
	private static final HashMap<UUID, String> cachedNames = new HashMap<>();

	@Override
	public String name() {
		return cachedNames.computeIfAbsent(uuid, MinecraftPerson::resolvePlayerNameFromUUID);
	}

	private static String resolvePlayerNameFromUUID(UUID uuid) {
		try {

			// TODO: Find some way to get logger here.
			System.out.println("Loading player name for " + uuid + "...");
			Optional<PlayerProfile> playerProfile = mojangAPI.getPlayerProfile(uuid.toString());
			if (playerProfile.isEmpty()) {
				throw new IllegalStateException("Unknown user with id " + uuid);
			}

			String username = playerProfile.get().getUsername();
			System.out.println("Got " + username);
			return username;

		} catch (ApiResponseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PersonType type() {
		return PersonType.MINECRAFT;
	}


}
