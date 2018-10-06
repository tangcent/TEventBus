package com.itangcent.event.spring.core;

import java.util.*;

public class ArrSwap<T extends Comparable<T>> {
    private T[] ts;

    public ArrSwap(T[] ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return Arrays.stream(ts).sorted().map(Objects::toString).reduce((s1, s2) -> s1 + "," + s2).orElse("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrSwap<?> arrSwap = (ArrSwap<?>) o;
        if (arrSwap.ts == null) {
            return ts == null;
        }
        if (ts == null) {
            return false;
        }
        if (ts.length != arrSwap.ts.length) {
            return false;
        }
        Set<T> set = new HashSet<>();
        Collections.addAll(set, ts);
        for (Object t : arrSwap.ts) {
            if (!set.contains(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.stream(ts).sorted().map(Objects::hashCode).reduce((h1, h2) -> h1 * 31 + h2).orElse(0);
    }
}
