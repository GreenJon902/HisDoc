package com.greenjon902.hisdoc.runners.papermc.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SubCommand {
	public abstract void run(@NotNull CommandSender sender, String label, ArgStream argStream);

	public abstract List<String> tabComplete(CommandSender sender, ArgStream argStream);
}
