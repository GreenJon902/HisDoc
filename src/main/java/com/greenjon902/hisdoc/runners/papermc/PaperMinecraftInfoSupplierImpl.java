package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.MinecraftInfoSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;

import java.util.UUID;
import java.util.logging.Logger;

public class PaperMinecraftInfoSupplierImpl implements MinecraftInfoSupplier {
	private final Logger logger;

	public PaperMinecraftInfoSupplierImpl(Logger logger) {
		this.logger = logger;
	}

	@Override
	public int getTicks(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE);
	}

	@Override
	public String getUsername(UUID uuid) {
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		if (name == null) {
			logger.fine("Bukkit cannot find name of player with uuid " + uuid);
			name = "Unknown";
		}
		return name;
	}
}
