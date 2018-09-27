package com.itangcent.event.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    public static <E> E first(Collection<E> collection) {
        return collection.iterator().next();
    }

    public static <E> boolean equals(E[] arr, E[] arr2) {
        if (arr == null) return arr2 == null;
        if (arr2 == null) return false;
        if (arr == arr2) return true;
        if (arr.length != arr2.length) return false;
        if (arr.length == 1) return arr[0].equals(arr2[0]);

        HashSet<E> set = new HashSet<>();
        java.util.Collections.addAll(set, arr);
        for (E e : arr2) {
            if (!set.contains(e)) {
                return false;
            }
        }
        return true;
    }
}
