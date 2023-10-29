package com.greenjon902.hisdoc.pageBuilder;

import java.util.Objects;
import java.util.UUID;

public class PageVariable {
	private final String text;
	private final String id;

	public PageVariable(String id) {
		this.id = id;
		this.text = id + "---" + UUID.randomUUID();  // Add some random uuid so it is much less likely to be broken
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		PageVariable that = (PageVariable) object;
		return Objects.equals(text, that.text) && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, id);
	}

	@Override
	public String toString() {
		return text;
	}
}
