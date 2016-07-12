# annoprops
Small java library for easy handling of auto serialization of your object field (eg. application settings).

[![Release](https://jitpack.io/v/Krzychek/annoprops.svg)](https://jitpack.io/#Krzychek/annoprops)

```java
class Server {
	@ConfigProperty("ServerPort")
	public int port = 7698; // default value: 7698, could be private as well
	...
}
// ... create object
Server server = new Server();
// ... create PropertyManager
PropertyManager propertyManager = PropertyManager.builder().withObjects(server) ...

File propertiesFile = new File("/path/to/file.properties");

// on start of application read persisted properties/settings
if (propertiesFile.exists())
	propertyManager.readPropertiesFromFile(propertiesFile);

// ... change field during runtime
server.port = 5555;

//  on shutdown persist setting to disk
propertyManager.savePropertiesToFile(propertiesFile);
```


# Download
##maven
```xml
<repositories>
  <repository>
  	<id>jitpack.io</id>
  	<url>https://jitpack.io</url>
  </repository>
</repositories>

<!-- dependency -->
<dependencies>
  <dependency>
  	<groupId>com.github.Krzychek.annoprops</groupId>
  	<artifactId>annoprops</artifactId>
  	<version>1.0</version>
  </dependency>
  
  <!-- optionally for spring helper -->
  <dependency>
  	<groupId>com.github.Krzychek.annoprops</groupId>
  	<artifactId>annoprops-springframework</artifactId>
  	<version>1.0</version>
  </dependency>
</dependencies>
```

##gradle
```groovy
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
...
dependencies {
	compile 'com.github.Krzychek.annoprops:annoprops:1.0'
	// optionally for spring helper
	compile 'com.github.Krzychek.annoprops:annoprops-springframework:1.0'
}
```

# Basic usage

Firstly we need to define classes with our properties, at the moment lib handles only single instances (with multi instances behaviour is not defined). Value of annotation is name of property in persisted file.

```
class Server {
	@ConfigProperty("ServerPort")
	private int port = 7698;
	...
}
enum Theme { WHITE, BLACK }
class UISettings {
	@ConfigProperty("Theme")
	private Theme theme = Theme.Black;
	...
}
```

Next we need to create PropertyManager with proper builder. This component handles basics of serialization/deserialization logic.

```
    Server server = new Server(); UISettings uiSettings = new UISettings();
    
    PropertyManager propertyManager = PropertyManager.builder()
            // use default serializers
            .withDefaultSerializers()
            // define objects which holds our properties
            .withObjects(server, uiSettings)
            .build();
```

Now we can save properties to file...

    File propertiesFile = new File("/path/to/file.properties");
    propertyManager.savePropertiesToFile(propertiesFile);

...or read previously saved ones.

    File propertiesFile = = new File("/path/to/file.properties");
    if (propertiesFile.exists())
        propertyManager.readPropertiesFromFile(propertiesFile);


# Dependency injection framework

## Basic bean
There is class `PropertyManagerHelperBean` which have methods `init()` and `destroy()`, annotated with `@PostConstruct` and `@PreDestroy`, which handles read/save on context creation and destroying. In some frameworks you'll have to call one of, or both methods explicitly (Some of frameworks does not handles shutting down context out of the box) 

## Spring
There is Spring helper factory defined in `annoprops-springframework` artefact:
```
@Bean
PropertyManagerHelperBean propertyManager(ListableBeanFactory beanFactory) throws IOException {
	return SpringframeworkAnnopropsBeanFactory.createWithSpringFactory(beanFactory, SETTINGS_FILE);
}
```

In such case you should mark proper bean class with `@PropertyHolder` annotation
```
@PropertyHolder
@Component
public class EventServer { ... }
```
