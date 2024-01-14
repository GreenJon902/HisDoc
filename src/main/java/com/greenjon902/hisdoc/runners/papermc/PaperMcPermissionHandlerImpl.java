package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.Permission;
import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.person.MinecraftPerson;
import com.greenjon902.hisdoc.sql.Dispatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * See {@link PermissionHandler}.
 * Ingame permissions are handled via Bukkit
 */
public class PaperMcPermissionHandlerImpl implements PermissionHandler {
	private final Dispatcher dispatcher;

	public PaperMcPermissionHandlerImpl(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public boolean hasPermission(int pid, Permission permission) {
		// This is difficult for offline players as bukkit permissions don't work.
		// An alternative would be use luck perms, I don't want to do that so for now I will
		// try to avoid that.

		switch (permission) {
			case LOAD_PAGE: return true; // We allow anyone to load the page
			case ADD_EVENT: return pid != 0;  // If they have permission to link accounts then we can allow it
			case EDIT_EVENT: {
				try {
					// Require player to be online
					Player player = Bukkit.getPlayer(((MinecraftPerson) dispatcher.getPersonInfo(pid).person()).uuid());
					if (player == null) {
						return false;
					}
					return player.hasPermission("hisdoc.editevent");
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}

		throw new IllegalStateException("PaperMcPermissionHandlerImpl cannot check for permission " + permission);
	}
}
