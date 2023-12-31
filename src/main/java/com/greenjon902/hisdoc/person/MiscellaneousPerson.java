package com.greenjon902.hisdoc.person;

import java.util.logging.Logger;

public record MiscellaneousPerson(String name) implements Person {

	@Override
	public PersonType type() {
		return PersonType.MISCELLANEOUS;
	}
}
