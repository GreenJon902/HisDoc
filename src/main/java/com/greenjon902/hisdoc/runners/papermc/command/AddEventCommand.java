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
import java.util.logging.Logger;

public class AddEventCommand implements CommandExecutor {
	private final Dispatcher dispatcher;
	private final PaperMcSessionHandlerImpl sessionHandler;
	private final String addEventUrl;
	private final Logger logger;

	public AddEventCommand(Dispatcher dispatcher, PaperMcSessionHandlerImpl sessionHandler, String addEventUrl, Logger logger) {
		this.dispatcher = dispatcher;
		this.sessionHandler = sessionHandler;
		this.addEventUrl = addEventUrl;
		this.logger = logger;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player playerSender) {
			logger.fine(sender.getName() + " ( " + playerSender.getUniqueId() + " ) ran /addevent");
			Integer pid;
			try {
				pid = dispatcher.getPersonIdFromMcUUID(playerSender.getUniqueId());
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
				for (String arg : args) {
					switch (arg) {
						case "--ignore-ip" -> {
							if (sender.hasPermission("hisdoc.addevent.ingoreip")) ignoreIp = true;
							else sender.sendMessage(Component.text("You do not have permission to use \"--ignore-ip\", ignoring!")
									.color(TextColor.color(0xFF0000)));
						}
						case "--persist" -> {
							if (sender.hasPermission("hisdoc.addevent.persist")) persist = true;
							else sender.sendMessage(Component.text("You do not have permission to use \"--persist\", ignoring!")
									.color(TextColor.color(0xFF0000)));
						}
						default -> sender.sendMessage(Component.text("Unknown argument \"" + arg + "\", ignoring!")
								.color(TextColor.color(0xFF0000)));
					}
				}

				String ip = null;
				if (playerSender.getAddress() != null && ignoreIp) ip = playerSender.getAddress().getHostString();

				logger.fine(sender.getName() + " was allocated the code \"" + code + "\", " +
						"they are limited to the ip \"" + ip + "\", " +
						"persist is set to " + persist);

				boolean replaced = sessionHandler.addVerification(code, pid, playerSender.getName(), ip, persist);
				if (replaced) sender.sendMessage(Component.text("Previous verifications for you were removed").color(TextColor.color(0xA32E00)));

				String url = sessionHandler.getPostUrl(pid);
				sender.sendMessage(Component.text("Use this link to add your event:").append(Component.newline()).append(
						Component.text(url)
								.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url))
				));
			}

		} else {
			sender.sendMessage("Only players can add events!");
		}
		return true;
	}
}
