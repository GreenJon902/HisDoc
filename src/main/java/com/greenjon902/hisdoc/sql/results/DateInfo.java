package com.greenjon902.hisdoc.sql.results;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public record DateInfo(@NotNull Type type, @NotNull Timestamp date1, @Nullable Precision precision, @Nullable Integer diff, @Nullable Precision diffType, @Nullable Date date2) {
	public DateInfo(@NotNull Type type, @NotNull Timestamp date1, @Nullable Precision precision, @Nullable Integer diff, @Nullable Precision diffType, @Nullable Date date2) {
		this.type = type;
		this.date1 = date1;
		this.precision = precision;
		this.diff = diff;
		this.diffType = diffType;
		this.date2 = date2;

		if (type == Type.CENTERED) {
			assert precision != null;
			assert diff != null;
			assert diffType != null;
			assert date2 == null;
		} else {
			assert precision == null;
			assert diff == null;
			assert diffType == null;
			assert date2 != null;
		}
	}
	public DateInfo(@NotNull String type, @NotNull Timestamp date1, @Nullable String precision, @Nullable Integer diff, @Nullable String diffType, @Nullable Date date2) {
		this(Type.decodeMap.get(type), date1, Precision.decodeMap.get(precision), diff, Precision.decodeMap.get(diffType), date2);
	}

	public static DateInfo centered(Timestamp center, Precision precision, int diff, Precision diffType) {
		return new DateInfo(Type.CENTERED, center, precision, diff, diffType, null);
	}

	public static DateInfo between(Timestamp date1, Date date2) {
		return new DateInfo(Type.BETWEEN, date1, null, null, null, date2);
	}

	public enum Type {
		CENTERED("c"), BETWEEN("b");

		public static final Map<String, Type> decodeMap;
		static {
			HashMap<String, Type> mutDecodeMap = new HashMap<>();
			for (Type dateType : values()) {
				mutDecodeMap.put(dateType.sqlId, dateType);
			}
			decodeMap = Collections.unmodifiableMap(mutDecodeMap);
		}

		private final String sqlId;

		Type(String sqlId) {
			this.sqlId = sqlId;
		}
	}

	public enum Precision {
		DAY("d"), HOUR("h"), MINUTE("m");

		public static final Map<String, Precision> decodeMap;
		static {
			HashMap<String, Precision> mutDecodeMap = new HashMap<>();
			for (Precision dateType : values()) {
				mutDecodeMap.put(dateType.sqlId, dateType);
			}
			decodeMap = Collections.unmodifiableMap(mutDecodeMap);
		}

		private final String sqlId;

		Precision(String sqlId) {
			this.sqlId = sqlId;
		}
	}
}
