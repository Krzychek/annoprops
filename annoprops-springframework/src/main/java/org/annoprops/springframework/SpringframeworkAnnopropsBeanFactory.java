package org.annoprops.springframework;

import org.annoprops.*;
import org.annoprops.annotations.PropertyHolder;
import org.springframework.beans.factory.ListableBeanFactory;

import java.io.File;
import java.util.Map;

public class SpringframeworkAnnopropsBeanFactory {

    /**
     * Uses {@link ListableBeanFactory} to get beans annotated with {@link org.annoprops.annotations.ConfigProperty} and create {@link PropertyManagerHelperBean}
     */
    public static PropertyManagerHelperBean createWithSpringFactory(
            ListableBeanFactory listableBeanFactory, File propertiesFile) {

        PropertyManagerBuilder propertyManagerBuilder = PropertyManager.builder()
                .withDefaultSerializers()
                .usingPropertyHolderProvider(createProvider(listableBeanFactory));

        return new PropertyManagerHelperBean(propertyManagerBuilder, propertiesFile);
    }

    /**
     * like {@link SpringframeworkAnnopropsBeanFactory#createWithSpringFactoryAndSerializers}, but ads default serializers as well
     */
    public static PropertyManagerHelperBean createWithSpringFactoryAndAdditionalSerializers(
            ListableBeanFactory listableBeanFactory, File propertiesFile, Map<Class<?>, PropertySerializer> propertySerializers) {

        PropertyManagerBuilder propertyManagerBuilder = PropertyManager.builder()
                .withDefaultSerializers()
                .withSerializers(propertySerializers)
                .usingPropertyHolderProvider(createProvider(listableBeanFactory));

        return new PropertyManagerHelperBean(propertyManagerBuilder, propertiesFile);
    }

    /**
     * like {@link SpringframeworkAnnopropsBeanFactory#createWithSpringFactory}, but uses {@param propertySerializers} instead of default ones
     */
    public static PropertyManagerHelperBean createWithSpringFactoryAndSerializers(
            ListableBeanFactory listableBeanFactory, File propertiesFile, Map<Class<?>, PropertySerializer> propertySerializers) {

        PropertyManagerBuilder propertyManagerBuilder = PropertyManager.builder()
                .withSerializers(propertySerializers)
                .usingPropertyHolderProvider(createProvider(listableBeanFactory));

        return new PropertyManagerHelperBean(propertyManagerBuilder, propertiesFile);
    }


    private static PropertyHolderProvider createProvider(ListableBeanFactory factory) {
        return () -> factory.getBeansWithAnnotation(PropertyHolder.class).values();
    }
}
