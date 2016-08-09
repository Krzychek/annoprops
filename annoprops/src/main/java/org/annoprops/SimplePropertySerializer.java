package org.annoprops;

import java.util.Arrays;
import java.util.Optional;


@SuppressWarnings("WeakerAccess")
public class SimplePropertySerializer implements PropertySerializer {

    @Override
    public String serialize(Object o) {
        if (o instanceof String) {
            return (String) o;
        }
        if (o instanceof Integer || o instanceof Double || o instanceof Float) {
            return o.toString();
        }
        if (o.getClass().isEnum()) {
            return ((Enum) o).name();
        }

        throw new IllegalArgumentException("Object class is not supported" + o.getClass().getName());
    }

    @Override
    public Optional deserialize(Class type, String value) {
        if (value == null)
            return Optional.empty();

        if (String.class.equals(type))
            return Optional.of(value);

        if (int.class.equals(type) || Integer.class.equals(type))
            return Optional.of(Integer.parseInt(value));

        if (double.class.equals(type) || Double.class.equals(type))
            return Optional.of(Double.parseDouble(value));

        if (float.class.equals(type) || Float.class.equals(type))
            return Optional.of(Float.parseFloat(value));

        if (type.isEnum()) {
            return Arrays.stream(type.getEnumConstants())
                    .map(Enum.class::cast)
                    .filter(e -> e.name().equalsIgnoreCase(value))
                    .findAny();
        }

        throw new IllegalArgumentException("Field class is not supported: " + type.getName());
    }
}
