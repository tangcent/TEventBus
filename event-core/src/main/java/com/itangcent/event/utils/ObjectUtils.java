package com.itangcent.event.utils;

import java.util.Objects;

public final class ObjectUtils {
    public static <T> T firstNonNull(T first, T second) {
        return first != null ? first : Objects.requireNonNull(second);
    }
}
