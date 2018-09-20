package com.itangcent.event.utils;

import java.util.*;
import java.util.function.Predicate;

public class Collections {

    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    public static <E> HashSet<E> newHashSet(Collection<E> collection) {
        return new HashSet<E>(collection);
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map) {
        return new HashMap<>(map);
    }

    public static <K, V> HashMap<K, V> newHashMap(K k, V v) {
        final HashMap<K, V> map = new HashMap<>();
        map.put(k, v);
        return map;
    }
}
