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

    private static Map<Class<?>, PropertySerializer> DEFAULT_SERIALIZERS;

    static {
        HashMap<Class<?>, PropertySerializer> classSerializers = new HashMap<>();

        SimplePropertySerializer simplePropertySerializer = new SimplePropertySerializer();
        classSerializers.put(Integer.class, simplePropertySerializer);
        classSerializers.put(int.class, simplePropertySerializer);
        classSerializers.put(Double.class, simplePropertySerializer);
        classSerializers.put(double.class, simplePropertySerializer);
        classSerializers.put(Float.class, simplePropertySerializer);
        classSerializers.put(float.class, simplePropertySerializer);
        classSerializers.put(String.class, simplePropertySerializer);
        classSerializers.put(Enum.class, simplePropertySerializer);
        DEFAULT_SERIALIZERS = Collections.unmodifiableMap(classSerializers);
    }

    private final Collection<Object> propertyHolders;

    private final Map<Class<?>, PropertySerializer> serializers;

    private final Properties properties;

    private PropertyManager(Collection<Object> propertyHolders, Map<Class<?>, PropertySerializer> serializers) {
        this.propertyHolders = propertyHolders;
        this.serializers = serializers;
        this.properties = new SortedProperties();
    }

    @SuppressWarnings("unused")
    public static Builder builder() {
        return new Builder();
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
		Class<?> fieldType = field.getType();
		String serialized = properties.getProperty(getName(field));
		if ( serialized == null )
			return NullableOptional.of(null);

        if (serializers.containsKey(fieldType))
            return serializers.get(fieldType).deserialize(fieldType, serialized);

        if (fieldType.isEnum() && serializers.containsKey(Enum.class))
            return serializers.get(Enum.class).deserialize(fieldType, serialized);

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

    public static class Builder {

        private final List<PropertyHolderProvider> propertyHolderProviders = new LinkedList<>();

        private final Collection<Object> propertyHolders = new ArrayList<>();

        private final Map<Class<?>, PropertySerializer> serializers = new HashMap<>();

        public Builder withSerializer(Class clazz, PropertySerializer serializer) {
            this.serializers.put(clazz, serializer);
            return this;
        }

        public Builder withSerializers(Map<Class<?>, PropertySerializer> serializers) {
            this.serializers.putAll(serializers);
            return this;
        }

        public Builder withDefaultSerializers() {
            this.serializers.putAll(DEFAULT_SERIALIZERS);
            return this;
        }

        public Builder withObject(Object propertyHolder) {
            propertyHolders.add(propertyHolder);
            return this;
        }

        public Builder withObjects(Collection<?> propertyHolder) {
            propertyHolders.addAll(propertyHolder);
            return this;
        }

        public Builder usingPropertyHolderProvider(PropertyHolderProvider propertyHolderProvider) {
            propertyHolderProviders.add(propertyHolderProvider);
            return this;
        }

        public Builder withObjects(Object... propertyHolder) {
            return withObjects(Arrays.asList(propertyHolder));
        }

        public PropertyManager build() {
            propertyHolderProviders.stream()
                    .map(PropertyHolderProvider::getPropertyHolders)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(() -> propertyHolders)); // wont work with pararell stream!

            return new PropertyManager(propertyHolders, serializers);
        }
    }

}
