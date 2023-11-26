package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record PersonData(@NotNull Type type, @NotNull String personData) {
	public PersonData(@NotNull Type type, @NotNull String personData) {
		this.type = type;
		this.personData = personData;
	}

	public PersonData(@NotNull String type, @NotNull String personData) {
		this(Type.decode(type), personData);
	}

	public static PersonData minecraft(String upid) {
		return new PersonData(Type.MINECRAFT, upid);
	}

	public static PersonData miscellaneous(String name) {
		return new PersonData(Type.MISCELLANEOUS, name);
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
