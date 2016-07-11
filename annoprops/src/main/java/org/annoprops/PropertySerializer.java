package org.annoprops;

@SuppressWarnings( "WeakerAccess" )
public interface PropertySerializer {

	String serialize(Object o);

	NullableOptional deserialize(Class type, String value);
}
