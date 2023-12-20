package com.greenjon902.hisdoc.runners.papermc.command;

import com.greenjon902.hisdoc.runners.papermc.HisDocRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class RestartHisDocCommand implements CommandExecutor {
	private final HisDocRunner hisDocRunner;
	private final Logger logger;

	public RestartHisDocCommand(HisDocRunner hisDocRunner, Logger logger) {
		this.hisDocRunner = hisDocRunner;
		this.logger = logger;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length != 1 || !args[0].equals("confirm")) {
			sender.sendMessage("Are you sure you want to do this? This will remove all current verifications and may break!\n" +
					"To confirm, please type /restarthisdoc confirm");
		} else {
			sender.sendMessage("Restarting HisDoc...");
			logger.warning("Restarting HisDoc... ---------------------------------------------------------------");
			hisDocRunner.onDisable();
			hisDocRunner.onEnable();
		}

		return true;
	}
}
