package org.annoprops;

import java.util.Optional;


@SuppressWarnings( "WeakerAccess" )
public class NullableOptional {

	private final static NullableOptional EMPTY = new NullableOptional();

	private final Object value;

	private final boolean present;

	private NullableOptional(Object value) {
		this.value = value;
		this.present = true;
	}

	private NullableOptional() {
		this.value = null;
		this.present = false;
	}

	public static NullableOptional empty() {
		return EMPTY;
	}

	public static NullableOptional of(Object value) {
		return new NullableOptional(value);
	}

	@SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
	public static NullableOptional fromOptional(Optional<?> optional) {
		return optional.isPresent() ? of(optional.get()) : empty();
	}

	public boolean isPresent() {
		return present;
	}

	public Object get() {
		return value;
	}
}
