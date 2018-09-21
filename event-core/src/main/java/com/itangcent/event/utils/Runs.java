package com.itangcent.event.utils;

public class Runs {

    public static void safeDo(ThrowaleRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {
        }
    }

    public static void uncheckDo(ThrowaleRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            ExceptionUtils.wrapAndThrow(e);
        }
    }

    public static <T, E extends Throwable> T safeCall(ThrowaleCallable<T, E> callable) {
        return safeCall(callable, null);
    }

    public static <T, E extends Throwable> T safeCall(ThrowaleCallable<T, E> callable, T defaultVal) {
        try {
            return callable.call();
        } catch (Throwable e) {
            return defaultVal;
        }
    }

    public static <T, E extends Throwable> T uncheckCall(ThrowaleCallable<T, E> callable) {
        try {
            return callable.call();
        } catch (Throwable e) {
            ExceptionUtils.wrapAndThrow(e);
            return null;//不会触发
        }
    }

}
