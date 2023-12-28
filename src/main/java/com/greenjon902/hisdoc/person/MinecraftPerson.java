package com.greenjon902.hisdoc.person;

import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

import java.util.HashMap;
import java.util.UUID;

public record MinecraftPerson(UUID uuid) implements Person {
	private static final Mojang mojang = new Mojang().connect();
	private static final HashMap<UUID, String> cachedNames = new HashMap<>();

	@Override
	public String name() {
		return cachedNames.computeIfAbsent(uuid, MinecraftPerson::resolvePlayerNameFromUUID);
	}

	private static String resolvePlayerNameFromUUID(UUID uuid) {
		// TODO: Find some way to get logger here.
		System.out.println("Loading player name for " + uuid + "...");
		PlayerProfile playerProfile = mojang.getPlayerProfile(uuid.toString());

		String username = playerProfile.getUsername();
		System.out.println("Got " + username);
		return username;
	}

	@Override
	public PersonType type() {
		return PersonType.MINECRAFT;
	}


}
