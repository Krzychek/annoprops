package org.annoprops;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;


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

    public PropertyManager getPropertyManager() {
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
