package org.annoprops;

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public interface PropertySerializer {

    String serialize(Object o);

    Optional deserialize(Class type, String value);
}
