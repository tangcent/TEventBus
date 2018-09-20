package com.itangcent.event.utils;

import java.lang.reflect.UndeclaredThrowableException;

public final class ExceptionUtils {

    /**
     * Throw a checked exception without adding the exception to the throws
     * clause of the calling method. For checked exceptions, this method throws
     * an UndeclaredThrowableException wrapping the checked exception. For
     * Errors and RuntimeExceptions, the original exception is rethrown.
     * <p>
     * The downside to using this approach is that invoking code which needs to
     * handle specific checked exceptions must sniff up the exception chain to
     * determine if the caught exception was caused by the checked exception.
     *
     * @param throwable The throwable to rethrow.
     * @param <R>       The type of the returned value.
     * @return Never actually returned, this generic type matches any type
     * which the calling site requires. "Returning" the results of this
     * method will satisfy the java compiler requirement that all code
     * paths return a value.
     * @since 3.5
     */
    public static <R> R wrapAndThrow(final Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        throw new UndeclaredThrowableException(throwable);
    }
}
