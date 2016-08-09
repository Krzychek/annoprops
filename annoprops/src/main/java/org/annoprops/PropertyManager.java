package org.annoprops;

import org.annoprops.annotations.ConfigProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;


@SuppressWarnings("WeakerAccess")
public class PropertyManager {

    private final Collection<Object> propertyHolders;

    private final Map<Class<?>, PropertySerializer> serializers;

    private final Properties properties;

    PropertyManager(Collection<Object> propertyHolders, Map<Class<?>, PropertySerializer> serializers) {
        this.propertyHolders = propertyHolders;
        this.serializers = serializers;
        this.properties = new Properties();
    }

    @SuppressWarnings("unused")
    public static PropertyManagerBuilder builder() {
        return new PropertyManagerBuilder();
    }

    @SuppressWarnings("unused")
    public void readPropertiesFromFile(String fileName) throws IOException {
        readPropertiesFromFile(new File(fileName));
    }

    @SuppressWarnings("unused")
    public void readPropertiesFromFile(File file) throws IOException {
        try (FileInputStream propertiesFile = new FileInputStream(file)) {
            properties.clear();
            properties.load(propertiesFile);

            setValuesInPropertyHolders();
        }
    }

    private void setValuesInPropertyHolders() {
        propertyHolders.stream()
                .flatMap(this::getPropertyDefs)
                .forEach(def -> {
                    Optional value = readValue(def.field);
                    if (value.isPresent()) {
                        def.field.setAccessible(true);
                        try {
                            def.field.set(def.holder, value.get());
                        } catch (IllegalAccessException ignored) {
                        } // impossible
                    }
                });
    }

    private Stream<PropertyDef> getPropertyDefs(Object o) {

        // stream of o and all super classes
        Stream.Builder<Class<?>> streamBuilder = Stream.builder();
        Class<?> aClass = o.getClass();
        do {
            streamBuilder.accept(aClass);
            aClass = aClass.getSuperclass();
        } while (aClass != null);

        return streamBuilder.build()
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .filter(field -> field.isAnnotationPresent(ConfigProperty.class))
                .map(field -> new PropertyDef(o, field));
    }

    private String getPropertyName(Field field) {
        String value = field.getAnnotation(ConfigProperty.class).value();
        return !value.isEmpty() ? value : field.getDeclaringClass().getName() + '#' + field.getName();
    }

    private Optional readValue(Field field) {
        String serialized = properties.getProperty(getPropertyName(field));
        if (serialized == null)
            return Optional.empty();

        Class<?> fieldType = field.getType();

        for (; fieldType != null; fieldType = fieldType.getSuperclass()) {
            PropertySerializer propertySerializer = serializers.get(fieldType);
            if (propertySerializer != null)
                return propertySerializer.deserialize(field.getType(), serialized);
        }

        throw new IllegalStateException("not found serializer for type" + field.getType().getCanonicalName());
    }

    @SuppressWarnings("unused")
    public void savePropertiesToFile(String fileName) throws IOException {
        savePropertiesToFile(new File(fileName));
    }

    @SuppressWarnings("unused")
    public void savePropertiesToFile(File file) throws IOException {
        try (FileOutputStream propertiesFile = new FileOutputStream(file)) {

            propertyHolders.stream()
                    .flatMap(this::getPropertyDefs)
                    .forEach(def -> {
                        def.field.setAccessible(true);
                        try {
                            getSerialized(def)
                                    .ifPresent(serialized -> properties.setProperty(getPropertyName(def.field), serialized));
                        } catch (IllegalAccessException ignored) {
                        } // impossible
                    });

            properties.store(propertiesFile, "IOMerge properties");
        }
    }

    private Optional<String> getSerialized(PropertyDef def) throws IllegalAccessException {
        Object value = def.field.get(def.holder);
        if (value == null) return Optional.empty();

        Class type = def.field.getType();

        PropertySerializer propertySerializer = serializers.get(type);
        if (propertySerializer != null)
            return Optional.of(propertySerializer.serialize(value));

        // special handle for enum
        if (type.isEnum() && serializers.containsKey(Enum.class))
            return Optional.of(serializers.get(Enum.class).serialize(value));

        throw new IllegalStateException("not found serializer for type: " + type.getCanonicalName());
    }

    private static class PropertyDef {
        public final Object holder;
        public final Field field;

        private PropertyDef(Object holder, Field field) {
            this.holder = holder;
            this.field = field;
        }
    }

}
