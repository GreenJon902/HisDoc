package com.greenjon902.hisdoc;

import com.greenjon902.hisdoc.webDriver.User;

/**
 * A class to help check if a {@link User} is allowed to do a certain action on the website.
 * See also {@link Permission}.
 */
public interface PermissionHandler {
	/**
	 * Checks if a person with that pid has the given permission.
	 *
	 * @param pid The pid of the person, or 0 for default (for unknown users)
	 * @return True if they have the permission
	 */
	boolean hasPermission(int pid, Permission permission);
}
