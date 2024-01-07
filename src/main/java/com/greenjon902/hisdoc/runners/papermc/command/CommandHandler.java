package com.greenjon902.hisdoc.runners.papermc.command;

import com.greenjon902.hisdoc.runners.papermc.HisDocRunner;
import com.greenjon902.hisdoc.runners.papermc.PaperMcSessionHandlerImpl;
import com.greenjon902.hisdoc.sql.Dispatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.checkerframework.checker.units.qual.A;
import org.codehaus.plexus.util.StringInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

// TODO: Add help command

public class CommandHandler implements TabExecutor {
	private final Map<String, SubCommand> actions;

	public CommandHandler(Dispatcher dispatcher, PaperMcSessionHandlerImpl sessionHandler, Logger logger, HisDocRunner hisDocRunner) {
		actions = Map.of(
				"add", new AddEventCommand(dispatcher, sessionHandler, logger),
				"restarthisdoc", new RestartHisDocCommand(hisDocRunner, logger)
		);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		ArgStream argStream = new ArgStream(args);
		if (!argStream.minimum(1)) {
			sender.sendMessage(
					Component.text("Too few arguments supplied, type ")
							.append(formatHelpMessage(label)));

		} else {
			String action = argStream.consume();
			SubCommand subCommand = actions.get(action);
			if (subCommand == null) {
				sender.sendMessage(
						Component.text("Unknown action, type")
								.append(formatHelpMessage(label)));
			} else {
				subCommand.run(sender, label, argStream);
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		ArgStream argStream = new ArgStream(args);

		// Arg 1 - Action
		if (argStream.remaining(1)) {
			return actions.keySet().stream().filter(string -> sender.hasPermission("hisdoc." + string)).toList();
		}

		// Arg 2... - Let action figure that out themselves
		String action = argStream.consume();
		SubCommand subCommand = actions.get(action);
		if (subCommand == null) {
			return null;
		} else {
			return subCommand.tabComplete(sender, argStream);
		}
	}

	protected static Component formatHelpMessage(String label) {
		return Component.text("/" + label + " help").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " help"))
				.append(Component.text(" to see usage!"));
	}
}
