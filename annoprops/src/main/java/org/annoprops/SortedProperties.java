package org.annoprops;

import java.util.*;


class SortedProperties extends Properties {

    private static final long serialVersionUID = -7852944216589332733L;

    @SuppressWarnings("unchecked")
    public Enumeration keys() {
        List<String> keyList = new ArrayList<>(super.size());
        keyList.addAll((Set<String>) (Set<?>) super.keySet());
        Collections.sort(keyList);
        return Collections.enumeration(keyList);
    }
}
