package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.McPlaytimeSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;

import java.util.UUID;

public class PaperMcPlaytimeSupplierImpl implements McPlaytimeSupplier {
	@Override
	public int getTicks(String uuid) {
		return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getStatistic(Statistic.PLAY_ONE_MINUTE);
	}
}
