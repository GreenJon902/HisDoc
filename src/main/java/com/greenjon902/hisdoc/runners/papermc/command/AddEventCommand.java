package com.greenjon902.hisdoc.runners.papermc.command;

import com.greenjon902.hisdoc.runners.papermc.PaperMcSessionHandlerImpl;
import com.greenjon902.hisdoc.sql.Dispatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

import static net.kyori.adventure.text.event.ClickEvent.Action.OPEN_URL;

public class AddEventCommand implements CommandExecutor {
	private final Dispatcher dispatcher;
	private final PaperMcSessionHandlerImpl sessionHandler;
	private final String addEventUrl;

	public AddEventCommand(Dispatcher dispatcher, PaperMcSessionHandlerImpl sessionHandler, String addEventUrl) {
		this.dispatcher = dispatcher;
		this.sessionHandler = sessionHandler;
		this.addEventUrl = addEventUrl;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player playerSender) {
			Integer pid;
			try {
				pid = dispatcher.getPersonIdFromMcUUID(playerSender.getUniqueId());
			} catch (SQLException e) {
				sender.sendMessage("Sorry, We had an error getting your person id :(");
				throw new RuntimeException(e);
			}
			System.out.println("Pid of user is " + pid);
			if (pid == null) {
				sender.sendMessage("Sorry, but you are not yet in our database, please contact your HisDoc administrator");
			} else {

				// So everything is good, we now need to add a verification
				String code = RandomStringUtils.random(7, true, true);

				String ip = null;
				if (playerSender.getAddress() != null && (args.length > 0 && !args[0].equals("--ignore-ip")))
					ip = playerSender.getAddress().getHostString();

				sessionHandler.addVerification(code, pid, playerSender.getName(), ip);
				sender.sendMessage(Component.text("Use this link to add your event:").append(Component.newline()).append(
						Component.text("http://" + addEventUrl + "?code="+code)
								.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + addEventUrl + "?code="+code))
				));
			}

		} else {
			sender.sendMessage("Only players can add events!");
		}
		return true;
	}
}
