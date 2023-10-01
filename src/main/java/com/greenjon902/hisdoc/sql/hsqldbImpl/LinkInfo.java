package com.greenjon902.hisdoc.sql.hsqldbImpl;

public record LinkInfo(String name, LinkType type, int id) {
	public enum LinkType {
		EVENT, PLAYER
	}

	public static LinkInfo event(String name, int id) {
		return new LinkInfo(name, LinkType.EVENT, id);
	}

	public static LinkInfo player(String name, int id) {
		return new LinkInfo(name, LinkType.PLAYER, id);
	}
}
