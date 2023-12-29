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
import java.util.logging.Logger;
import java.util.stream.Stream;

// TODO: Add help command
// TODO: Load commands from a map

public class CommandHandler implements TabExecutor {
	private final AddEventCommand addEventCommand;
	private final RestartHisDocCommand restartHisDoc;

	public CommandHandler(Dispatcher dispatcher, PaperMcSessionHandlerImpl sessionHandler, Logger logger, HisDocRunner hisDocRunner) {
		this.addEventCommand = new AddEventCommand(dispatcher, sessionHandler, logger);
		this.restartHisDoc = new RestartHisDocCommand(hisDocRunner, logger);
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
			switch (action) {
				case "addevent" -> addEventCommand.run(sender, argStream);
				case "restarthisdoc" -> restartHisDoc.run(sender, label, argStream);
				default -> Component.text("Unknown action, type")
						.append(formatHelpMessage(label));
			}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		ArgStream argStream = new ArgStream(args);
		if (argStream.remaining(1)) {
			return List.of("add", "restartHisDoc");
		}

		String action = argStream.consume();
		return switch (action) {
			case "addevent" -> addEventCommand.tabComplete(sender, argStream);
			case "restarthisdoc" -> restartHisDoc.tabComplete(sender, argStream);
			default -> null;
		};
	}

	protected static Component formatHelpMessage(String label) {
		return Component.text("/" + label + " help").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, label + " help"))
				.append(Component.text(" help to see usage!"));
	}
}
