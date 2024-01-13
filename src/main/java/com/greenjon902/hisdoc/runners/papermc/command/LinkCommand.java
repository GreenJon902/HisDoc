package com.greenjon902.hisdoc.runners.papermc.command;

import com.greenjon902.hisdoc.runners.papermc.PaperMcSessionHandlerImpl;
import com.greenjon902.hisdoc.sql.Dispatcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class LinkCommand extends SubCommand {
	private final Dispatcher dispatcher;
	private final PaperMcSessionHandlerImpl sessionHandler;
	private final Logger logger;

	public LinkCommand(Dispatcher dispatcher, PaperMcSessionHandlerImpl sessionHandler, Logger logger) {

		this.dispatcher = dispatcher;
		this.sessionHandler = sessionHandler;
		this.logger = logger;
	}

	@Override
	public void run(@NotNull CommandSender sender, String label, ArgStream argStream) {
		if (sender instanceof Player player) {
			if (!argStream.minimum(1)) {
				player.sendMessage("You must have a code to link to your account");
				return;
			}

			String code = argStream.consume();
			logger.fine(player + " ran the link command with code \"" + code + "\"");
			Integer pid;
			try {
				pid = dispatcher.getPersonIdFromMinecraftUUID(player.getUniqueId());
			} catch (SQLException e) {
				player.sendMessage("A sql error has occurred");
				throw new RuntimeException(e);
			}
			logger.finer("Got pid " + pid);
			if (pid == null) {
				player.sendMessage("No pid could be found, are you in our database?");
			} else {
				boolean result = sessionHandler.finishLink(code, pid);
				logger.fine("Result was " + result);
				if (result) {
					player.sendMessage("You have been successfully linked");
				} else {
					player.sendMessage("Unknown code");
				}
			}
		} else {
			sender.sendMessage("Only players link accounts");
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, ArgStream argStream) {
		return null;
	}
}
