package org.annoprops;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;


/**
 * Helper class, providing init and destroy methods, to use with DI frameworks
 */
public class PropertyManagerHelperBean {

    private final PropertyManagerBuilder propertyManagerBuilder;
    private final File propertiesFile;
    private PropertyManager propertyManager;

    public PropertyManagerHelperBean(PropertyManagerBuilder propertyManagerBuilder, File propertiesFile) {
        this.propertyManagerBuilder = propertyManagerBuilder;
        this.propertiesFile = propertiesFile;
    }

    @SuppressWarnings("unused")
    public static PropertyManagerHelperBean createWithFileAndObjects(File propertiesFile, Collection<?> objects) {
        return new PropertyManagerHelperBean(
                PropertyManager.builder().withObjects(objects),
                propertiesFile);
    }

    @SuppressWarnings("unused")
    public static PropertyManagerHelperBean createWithFileAndObjects(File propertiesFile, Collection<?> objects, Map<Class<?>, PropertySerializer> serializers) {
        return new PropertyManagerHelperBean(
                PropertyManager.builder()
                        .withObjects(objects)
                        .withSerializers(serializers),
                propertiesFile);
    }

    private PropertyManager getPropertyManager() {
        if (propertyManager == null)
            propertyManager = propertyManagerBuilder.build();

        return propertyManager;
    }

    @PostConstruct
    public void init() throws IOException {
        if (propertiesFile.exists())
            getPropertyManager().readPropertiesFromFile(propertiesFile);
    }

    @PreDestroy
    public void destroy() throws IOException {
        getPropertyManager().savePropertiesToFile(propertiesFile);
    }
}
