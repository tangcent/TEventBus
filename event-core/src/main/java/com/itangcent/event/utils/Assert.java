package com.itangcent.event.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public final class Assert {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(format(message, args));
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void equals(Object a, Object b, String message, Object... args) {
        if (Objects.equals(a, b)) {
            throw new IllegalArgumentException(format(message, args));
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message, Object... args) {
        if (object == null) {
            throw new IllegalArgumentException(format(message, args));
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void hasLength(String text, String message) {
        if (!StringUtils.isNotEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasLength(String text) {
        hasLength(text,
                "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    public static void hasText(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasText(String text) {
        hasText(text,
                "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    public static void doesNotContain(String textToSearch, String substring, String message) {
        if (StringUtils.isNotEmpty(textToSearch) && StringUtils.isNotEmpty(substring) &&
                textToSearch.contains(substring)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void doesNotContain(String textToSearch, String substring) {
        doesNotContain(textToSearch, substring,
                "[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Object[] array) {
        notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    public static void noNullElements(Object[] array) {
        noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }

    public static void notEmpty(Collection collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Collection collection) {
        notEmpty(collection,
                "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static void notEmpty(Map map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Map map) {
        notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    public static void isInstanceOf(Class<?> clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }

    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(
                    (StringUtils.isNotEmpty(message) ? message + " " : "") +
                            "Object of class [" + (obj != null ? obj.getClass().getName() : "null") +
                            "] must be an instance of " + type);
        }
    }

    public static void isInstanceOf(Class<?> type, Object obj, String message, Object... args) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(format(message, args));
        }
    }

    public static void notInstanceOf(Class<?> type, Object obj, String message, Object... args) {
        notNull(type, "Type to check against must not be null");
        if (type.isInstance(obj)) {
            throw new IllegalArgumentException(format(message, args));
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType) {
        isAssignable(superType, subType, "");
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
        }
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean expression) {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }

    static String format(String template, Object... args) {
        template = String.valueOf(template); // null -> "null"

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template, templateStart, template.length());

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }
}
