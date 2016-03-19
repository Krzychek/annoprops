package org.annoprops;

import java.util.Collection;


@SuppressWarnings( "unused" )
public class ManagerFactory {

	public static PropertyManager createWithObjects(Collection<?> propertyHolders) {
		return new PropertyManager.Builder().withObjects(propertyHolders).build();

	}

	public static PropertyManager createWithObject(Collection<?> propertyHolders) {
		return new PropertyManager.Builder().withObjects(propertyHolders).build();

	}
}
