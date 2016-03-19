package org.annoprops;

import javax.annotation.Nonnull;


@SuppressWarnings( "WeakerAccess" )
public interface PropertySerializer {

	@Nonnull
	String serialize(Object o);

	@Nonnull
	NullableOptional deserialize(Class type, String value);
}
