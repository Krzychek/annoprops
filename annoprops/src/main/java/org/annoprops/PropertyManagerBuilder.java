package org.annoprops;

import java.util.*;
import java.util.stream.Collectors;

public class PropertyManagerBuilder {

    private final static Map<Class<?>, PropertySerializer> DEFAULT_SERIALIZERS = Collections.unmodifiableMap(new HashMap<Class<?>, PropertySerializer>() {{
        SimplePropertySerializer simplePropertySerializer = new SimplePropertySerializer();
        put(Integer.class, simplePropertySerializer);
        put(int.class, simplePropertySerializer);
        put(Double.class, simplePropertySerializer);
        put(double.class, simplePropertySerializer);
        put(Float.class, simplePropertySerializer);
        put(float.class, simplePropertySerializer);
        put(String.class, simplePropertySerializer);
        put(Enum.class, simplePropertySerializer);
    }});

    private final List<PropertyHolderProvider> propertyHolderProviders = new LinkedList<>();

    private final Collection<Object> propertyHolders = new ArrayList<>();

    private final Map<Class<?>, PropertySerializer> serializers = new HashMap<>();

    public PropertyManagerBuilder withSerializer(Class clazz, PropertySerializer serializer) {
        this.serializers.put(clazz, serializer);
        return this;
    }

    public PropertyManagerBuilder withSerializers(Map<Class<?>, PropertySerializer> serializers) {
        this.serializers.putAll(serializers);
        return this;
    }

    public PropertyManagerBuilder withDefaultSerializers() {
        this.serializers.putAll(DEFAULT_SERIALIZERS);
        return this;
    }

    public PropertyManagerBuilder withObject(Object propertyHolder) {
        propertyHolders.add(propertyHolder);
        return this;
    }

    public PropertyManagerBuilder withObjects(Collection<?> propertyHolder) {
        propertyHolders.addAll(propertyHolder);
        return this;
    }

    public PropertyManagerBuilder usingPropertyHolderProvider(PropertyHolderProvider propertyHolderProvider) {
        propertyHolderProviders.add(propertyHolderProvider);
        return this;
    }

    public PropertyManagerBuilder withObjects(Object... propertyHolder) {
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
