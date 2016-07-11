package org.annoprops;

import java.util.Collection;


@FunctionalInterface
public interface PropertyHolderProvider {

    Collection<?> getPropertyHolders();
}
