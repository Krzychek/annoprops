package org.annoprops;

import java.util.Arrays;


@SuppressWarnings("WeakerAccess")
public class SimplePropertySerializer implements PropertySerializer {

    private static final String NULL = "!";

    @Override
    public String serialize(Object o) {
        if (o == null)
            return NULL;

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
    public NullableOptional deserialize(Class type, String value) {
        if (value == null)
            return NullableOptional.empty();

        if (NULL.equals(value))
            return NullableOptional.of(null);

        if (String.class.equals(type))
            return NullableOptional.of(value);

        if (int.class.equals(type) || Integer.class.equals(type))
            return NullableOptional.of(Integer.parseInt(value));

        if (double.class.equals(type) || Double.class.equals(type))
            return NullableOptional.of(Double.parseDouble(value));

        if (float.class.equals(type) || Float.class.equals(type))
            return NullableOptional.of(Float.parseFloat(value));

        if (type.isEnum()) {
            return NullableOptional.fromOptional( //
                    Arrays.stream(type.getEnumConstants()).map(Enum.class::cast) //
                            .filter(e -> e.name().equalsIgnoreCase(value)) //
                            .findAny());
        }

        throw new IllegalArgumentException("Object class is not supported" + type.getName());
    }
}
