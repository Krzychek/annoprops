package org.annoprops;

import org.annoprops.annotations.ConfigProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("WeakerAccess")
public class PropertyManager {

    private final Collection<Object> propertyHolders;

    private final Map<Class<?>, PropertySerializer> serializers;

    private final Properties properties;

    PropertyManager(Collection<Object> propertyHolders, Map<Class<?>, PropertySerializer> serializers) {
        this.propertyHolders = propertyHolders;
        this.serializers = serializers;
        this.properties = new SortedProperties();
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
    public void readPropertiesFromFile(File fileName) throws IOException {
        try (FileInputStream propertiesFile = new FileInputStream(fileName)) {
            properties.clear();
            properties.load(propertiesFile);

            setValuesInPropertyHolders();
        }
    }

    private void setValuesInPropertyHolders() {
        propertyHolders.forEach(propertyHolder -> //
                getAnnotatedFields(propertyHolder).forEach(field -> {
                    NullableOptional value = readValue(field);
                    if (value.isPresent()) {
                        field.setAccessible(true);
                        try {
                            field.set(propertyHolder, value.get());
                            field.set(propertyHolder, value.get());
                        } catch (IllegalAccessException ignored) {
                        } // impossible
                    }
                }));
    }

    private List<Field> getAnnotatedFields(Object o) {
        List<Class<?>> oClasses = new LinkedList<>();
        Class<?> aClass = o.getClass();
        do {
            oClasses.add(aClass);
            aClass = aClass.getSuperclass();
        } while (aClass != null);

        return oClasses.stream() //
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredFields())) //
                .filter(field -> field.isAnnotationPresent(ConfigProperty.class)) //
                .collect(Collectors.toList());
    }

    private String getName(Field field) {
        String value = field.getAnnotation(ConfigProperty.class).value();
        return !value.isEmpty() ? value : field.getDeclaringClass().getName() + '#' + field.getName();
    }

    private NullableOptional readValue(Field field) {
        String serialized = properties.getProperty(getName(field));
        if (serialized == null)
            return NullableOptional.empty();

        Class<?> fieldType = field.getType();

        for (; fieldType != null; fieldType = fieldType.getSuperclass()) {
            PropertySerializer propertySerializer = serializers.get(fieldType);
            if (propertySerializer != null)
                return propertySerializer.deserialize(field.getType(), serialized);
        }

        throw new IllegalStateException("not found serializer for type" + fieldType.getCanonicalName());
    }

    @SuppressWarnings("unused")
    public void savePropertiesToFile(String fileName) throws IOException {
        savePropertiesToFile(new File(fileName));
    }

    @SuppressWarnings("unused")
    public void savePropertiesToFile(File file) throws IOException {
        try (FileOutputStream propertiesFile = new FileOutputStream(file)) {

            propertyHolders.forEach(propertyHolder -> //
                    getAnnotatedFields(propertyHolder).forEach(field -> {
                        field.setAccessible(true);
                        try {
                            properties.setProperty(getName(field),
                                    getSerialized(field.get(propertyHolder), field.getType()));
                        } catch (IllegalAccessException ignored) {
                        } // impossible
                    }));

            properties.store(propertiesFile, "IOMerge properties");
        }
    }

    private String getSerialized(Object obj, Class<?> type) {

        if (serializers.containsKey(type))
            return serializers.get(type).serialize(obj);

        if (type.isEnum() && serializers.containsKey(Enum.class))
            return serializers.get(Enum.class).serialize(obj);

        throw new IllegalStateException("not found serializer for type" + type.getCanonicalName());
    }

}
