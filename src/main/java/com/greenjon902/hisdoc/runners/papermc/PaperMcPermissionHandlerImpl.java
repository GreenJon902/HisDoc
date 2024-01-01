package com.greenjon902.hisdoc.runners.papermc;

import com.greenjon902.hisdoc.Permission;
import com.greenjon902.hisdoc.PermissionHandler;
import com.greenjon902.hisdoc.person.MinecraftPerson;
import com.greenjon902.hisdoc.person.Person;
import com.greenjon902.hisdoc.sql.Dispatcher;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class PaperMcPermissionHandlerImpl implements PermissionHandler {
	@Override
	public boolean hasPermission(int pid, Permission permission) {
		// This is difficult for offline players as bukkit permissions don't work.
		// An alternative would be use luck perms, I don't want to do that so for now I will
		// try to avoid that.

		switch (permission) {
			case LOAD_PAGE:  // We allow anyone to load the page
			case ADD_EVENT: return true;  // If they had the permission to type the command then they can add an event
		}

		throw new IllegalStateException("PaperMcPermissionHandlerImpl cannot check for permission " + permission);
	}
}
