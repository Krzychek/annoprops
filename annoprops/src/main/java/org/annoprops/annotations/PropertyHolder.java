package org.annoprops.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * Helper annotations, eg to use with dependency injection framework
 */
@Target( ElementType.TYPE )
public @interface PropertyHolder {}
