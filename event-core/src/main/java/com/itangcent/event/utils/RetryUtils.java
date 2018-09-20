package com.itangcent.event.utils;

import com.itangcent.event.annotation.Retry;

import java.lang.reflect.InvocationTargetException;

public class RetryUtils {

    public static int maxRetryTimes(Retry retry, final Throwable e, int defaultRetryTimes) {
        int retryTimes = defaultRetryTimes;
        if (retry != null) {
            if (!RetryUtils.checkOn(retry.on(), e)) {
                return 0;
            }
            if (retry.times() != -1) {
                retryTimes = retry.times();
            }
        }
        return retryTimes;
    }

    public static boolean checkOn(Retry retry, final Throwable e) {
        return checkOn(retry.on(), e);
    }

    /**
     * check the Throwable is assignable from anyone of the given exceptions
     */
    public static boolean checkOn(Class<? extends Throwable>[] exceptions, final Throwable e) {

        Throwable throwable = null;
        if (e instanceof Error) {
            throwable = e.getCause();
        }
        if (e instanceof InvocationTargetException) {
            throwable = e.getCause();
        }
        if (throwable == null) {
            throwable = e;
        }
        Class eCls = throwable.getClass();
        for (Class<? extends Throwable> exception : exceptions) {
            if (exception.isAssignableFrom(eCls)) {
                return true;
            }
        }
        return false;
    }

}
