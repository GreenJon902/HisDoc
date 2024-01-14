package com.greenjon902.hisdoc;

public enum Permission {
	/**
	 * Can this person load a page, this applies to all pages on the WebDriver level.
	 * Without this permission, the connection will just disconnect.
	 */
	LOAD_PAGE,
	/**
	 * Can this person add an event.
	 */
 	ADD_EVENT,

	/**
	 * Can this person modify events.
	 */
	EDIT_EVENT
}
