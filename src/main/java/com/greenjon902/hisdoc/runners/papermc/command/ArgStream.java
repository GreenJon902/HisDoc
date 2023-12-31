package com.greenjon902.hisdoc.runners.papermc.command;

public class ArgStream {
	private final String[] args;
	private int pointer;

	public ArgStream(String[] args) {
		this.args = args;
		pointer = 0;
	}

	/**
	 * Checks if there is a minimum of n arguments remaining.
	 */
	public boolean minimum(int n) {
		return args.length >= pointer + n;
	}

	/**
	 * Gets the next argument and increments the pointer.
	 */
	public String consume() {
		String string = args[pointer];
		pointer += 1;
		return string;
	}

	/**
	 * Checks if there is exactly n arguments remaining.
	 */
	public boolean remaining(int n) {
		return args.length == pointer + n;
	}

	/**
	 * Returns the remaining parameters in an array and move the pointer to the end
	 */
	public String[] consumeRemaining() {
		String[] strings = new String[args.length - pointer - 1];
		System.arraycopy(args, pointer, strings, 0, strings.length);
		pointer = args.length;
		return strings;
	}
}
