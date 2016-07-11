package org.annoprops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;


class SortedProperties extends Properties {

	private static final long serialVersionUID = -7852944216589332733L;

	@SuppressWarnings( "unchecked" )
	public Enumeration keys() {
		List<String> keyList = new ArrayList<>(super.size());
		keyList.addAll((Set<String>) (Set<?>) super.keySet());
		Collections.sort(keyList);
		return Collections.enumeration(keyList);
	}
}
