package org.annoprops;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class PropertyManagerBuilder {

    private final List<PropertyHolderProvider> propertyHolderProviders = new LinkedList<>();
    private final Collection<Object> propertyHolders = new ArrayList<>();
    private final Map<Class<?>, PropertySerializer> serializers = new HashMap<>();

    private static Map<Class<?>, PropertySerializer> getDefaultSerializers() {
        SimplePropertySerializer simplePropertySerializer = new SimplePropertySerializer();

        return new HashMap<Class<?>, PropertySerializer>() {{
            put(Integer.class, simplePropertySerializer);
            put(int.class, simplePropertySerializer);
            put(Double.class, simplePropertySerializer);
            put(double.class, simplePropertySerializer);
            put(Float.class, simplePropertySerializer);
            put(float.class, simplePropertySerializer);
            put(String.class, simplePropertySerializer);
            put(Enum.class, simplePropertySerializer);
        }};
    }

    @SuppressWarnings("unused")
    public PropertyManagerBuilder withSerializer(Class clazz, PropertySerializer serializer) {
        this.serializers.put(clazz, serializer);
        return this;
    }

    @SuppressWarnings("unused")
    public PropertyManagerBuilder withSerializers(Map<Class<?>, PropertySerializer> serializers) {
        this.serializers.putAll(serializers);
        return this;
    }

    @SuppressWarnings("unused")
    public PropertyManagerBuilder withDefaultSerializers() {
        this.serializers.putAll(getDefaultSerializers());
        return this;
    }

    @SuppressWarnings("unused")
    public PropertyManagerBuilder withObject(Object propertyHolder) {
        propertyHolders.add(propertyHolder);
        return this;
    }

    @SuppressWarnings("unused")
    public PropertyManagerBuilder withObjects(Collection<?> propertyHolder) {
        propertyHolders.addAll(propertyHolder);
        return this;
    }

    @SuppressWarnings("unused")
    public PropertyManagerBuilder usingPropertyHolderProvider(PropertyHolderProvider propertyHolderProvider) {
        propertyHolderProviders.add(propertyHolderProvider);
        return this;
    }

    @SuppressWarnings("unused")
    public PropertyManagerBuilder withObjects(Object... propertyHolder) {
        return withObjects(Arrays.asList(propertyHolder));
    }

    @SuppressWarnings("unused")
    public PropertyManager build() {
        propertyHolderProviders.stream()
                .map(PropertyHolderProvider::getPropertyHolders)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(() -> propertyHolders)); // wont work with pararell stream!

        return new PropertyManager(propertyHolders, serializers);
    }
}
