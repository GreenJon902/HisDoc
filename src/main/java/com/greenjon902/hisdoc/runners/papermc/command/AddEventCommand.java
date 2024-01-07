package com.greenjon902.hisdoc.runners.papermc.command;

import com.greenjon902.hisdoc.runners.papermc.PaperMcSessionHandlerImpl;
import com.greenjon902.hisdoc.sql.Dispatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AddEventCommand extends SubCommand {
	private final Dispatcher dispatcher;
	private final PaperMcSessionHandlerImpl sessionHandler;
	private final Logger logger;

	public AddEventCommand(Dispatcher dispatcher, PaperMcSessionHandlerImpl sessionHandler, Logger logger) {
		this.dispatcher = dispatcher;
		this.sessionHandler = sessionHandler;
		this.logger = logger;
	}

	public void run(@NotNull CommandSender sender, String label, @NotNull ArgStream argStream) {
		if (sender instanceof Player playerSender) {
			logger.fine(sender.getName() + " ( " + playerSender.getUniqueId() + " ) ran the add event command");

			if (sender.hasPermission("hisdoc.add")) {

				Integer pid;
				try {
					pid = dispatcher.getPersonIdFromMinecraftUUID(playerSender.getUniqueId());
				} catch (SQLException e) {
					sender.sendMessage("Sorry, We had an error getting your person id :(");
					throw new RuntimeException(e);
				}
				logger.fine(sender.getName() + "'s pid is " + pid);
				if (pid == null) {
					sender.sendMessage("Sorry, but you are not yet in our database, please contact your HisDoc administrator");
				} else {

					// So everything is good, we now need to add a verification
					String code = RandomStringUtils.random(7, true, true);

					boolean ignoreIp = false;
					boolean persist = false;
					for (String arg : argStream.consumeRemaining()) {
						switch (arg) {
							case "--ignore-ip" -> {
								if (sender.hasPermission("hisdoc.add.ingoreip")) ignoreIp = true;
								else
									sender.sendMessage(Component.text("You do not have permission to use \"--ignore-ip\", ignoring!")
											.color(TextColor.color(0xFF0000)));
							}
							case "--persist" -> {
								if (sender.hasPermission("hisdoc.add.persist")) persist = true;
								else
									sender.sendMessage(Component.text("You do not have permission to use \"--persist\", ignoring!")
											.color(TextColor.color(0xFF0000)));
							}
							default -> sender.sendMessage(Component.text("Unknown argument \"" + arg + "\", ignoring!")
									.color(TextColor.color(0xFF0000)));
						}
					}

					String ip = null;
					if (playerSender.getAddress() != null && !ignoreIp) ip = playerSender.getAddress().getHostString();

					logger.fine(sender.getName() + " was allocated the code \"" + code + "\", " +
							"they are limited to the ip \"" + ip + "\", " +
							"persist is set to " + persist);

					boolean replaced = sessionHandler.addVerification(code, pid, playerSender.getName(), ip, persist);
					if (replaced)
						sender.sendMessage(Component.text("Previous verifications for you were removed").color(TextColor.color(0xA32E00)));

					String url = sessionHandler.getPostUrl(pid);
					sender.sendMessage(Component.text("Use this link to add your event:").append(Component.newline()).append(
							Component.text(url)
									.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url))
					));
				}
			} else {
				sender.sendMessage("Sorry, you do not have permission to do this action!");
			}
		} else {
			sender.sendMessage("Only players can add events!");
		}
	}

	public List<String> tabComplete(CommandSender sender, ArgStream argStream) {
		// All args are now just switches, so add any that have permissions, then remove any that have been used already
		ArrayList<String> list = new ArrayList<>();
		if (sender.hasPermission("hisdoc.addevent.ingoreip")) {
			list.add("--ignore-ip");
		}
		if (sender.hasPermission("hisdoc.addevent.persist")) {
			list.add("--persist");
		}

		for (String arg : argStream.consumeRemaining()) {
			list.remove(arg); // Remove if it exists
		}

		return list;
	}
}
