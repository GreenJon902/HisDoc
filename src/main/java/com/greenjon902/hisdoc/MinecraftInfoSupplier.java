package com.greenjon902.hisdoc;

import java.util.UUID;


public interface MinecraftInfoSupplier {
	int getTicks(UUID uuid);

	String getUsername(UUID uuid);
}
