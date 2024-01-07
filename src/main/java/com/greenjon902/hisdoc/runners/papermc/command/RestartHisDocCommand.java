package com.greenjon902.hisdoc.runners.papermc.command;

import com.greenjon902.hisdoc.runners.papermc.HisDocRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

public class RestartHisDocCommand extends SubCommand {
	private final HisDocRunner hisDocRunner;
	private final Logger logger;

	public RestartHisDocCommand(HisDocRunner hisDocRunner, Logger logger) {
		this.hisDocRunner = hisDocRunner;
		this.logger = logger;
	}

	public void run(@NotNull CommandSender sender, String label, ArgStream argStream) {
		if (!sender.hasPermission("hisdoc.restarthisdoc")) {
			sender.sendMessage("Sorry, you do not have permission to do this action!");


		} else {
			if (argStream.remaining(0) || !argStream.consume().equals("confirm")) {
				sender.sendMessage("Are you sure you want to do this? This will remove all current verifications and may break!\n" +
						"To confirm, please type /" + label + " restarthisdoc confirm");
			} else {
				sender.sendMessage("Restarting HisDoc...");
				logger.warning("Restarting HisDoc... ---------------------------------------------------------------");
				hisDocRunner.onDisable();
				hisDocRunner.onEnable();
			}
		}
	}

	public List<String> tabComplete(CommandSender sender, ArgStream argStream) {
		// Don't return the argument, let the user type it, so they have more time to think about it
		return null;
	}
}
