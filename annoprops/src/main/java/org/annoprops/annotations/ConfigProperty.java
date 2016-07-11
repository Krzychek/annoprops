package org.annoprops.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * annotates fields that should be taken into account by {@link org.annoprops.PropertyManager}
 */
@SuppressWarnings( "WeakerAccess" )
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ConfigProperty {

	/**
	 * defines how property would be named in property file after serialization,
	 * at the moment should be unique for the app
	 * <p>
	 * used also for deserialization, so should not be renamed if one wants past names to be renamed
	 */
	String value() default "";
}
