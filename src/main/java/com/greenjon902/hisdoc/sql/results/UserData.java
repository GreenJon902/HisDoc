package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record UserData(@NotNull Type type, @NotNull String userData) {
	public UserData(@NotNull Type type, @NotNull String userData) {
		this.type = type;
		this.userData = userData;
	}

	public UserData(@NotNull String type, @NotNull String userData) {
		this(Type.decode(type), userData);
	}

	public static UserData minecraft(String uuid) {
		return new UserData(Type.MINECRAFT, uuid);
	}

	public static UserData miscellaneous(String name) {
		return new UserData(Type.MISCELLANEOUS, name);
	}

	public enum Type {
		MINECRAFT("mc"), MISCELLANEOUS("misc");

		private static final Map<String, Type> decodeMap;
		static {
			HashMap<String, Type> mutDecodeMap = new HashMap<>();
			for (Type type : values()) {
				mutDecodeMap.put(type.sqlId.toLowerCase(), type);
			}
			decodeMap = Collections.unmodifiableMap(mutDecodeMap);
		}

		public static Type decode(String string) {
			if (string == null) return null;
			return decodeMap.get(string.toLowerCase());
		}

		private final String sqlId;

		Type(String sqlId) {
			this.sqlId = sqlId;
		}
	}
}
