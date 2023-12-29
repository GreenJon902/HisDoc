package com.greenjon902.hisdoc.person;

import com.greenjon902.hisdoc.MinecraftInfoSupplier;
import java.util.UUID;

public record MinecraftPerson(UUID uuid, MinecraftInfoSupplier minecraftInfoSupplier) implements Person {
	@Override
	public String name() {
		return minecraftInfoSupplier.getUsername(uuid);
	}

	public int ticks() {
		return minecraftInfoSupplier.getTicks(uuid);
	}

	@Override
	public PersonType type() {
		return PersonType.MINECRAFT;
	}


}
