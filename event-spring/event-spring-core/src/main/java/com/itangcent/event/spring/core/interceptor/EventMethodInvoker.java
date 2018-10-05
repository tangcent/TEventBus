package com.itangcent.event.spring.core.interceptor;


public interface EventMethodInvoker extends Runnable {

    /**
     * Invoke the event method defined by this instance. Wraps any exception
     * that is thrown during the invocation in a {@link EventMethodInvoker.ThrowableWrapper}.
     *
     * @return the result of the operation
     * @throws EventMethodInvoker.ThrowableWrapper if an error occurred while invoking the operation
     */
    Object invoke() throws ThrowableWrapper;

    @Override
    default void run() {
        invoke();
    }

    /**
     * Wrap any exception thrown while invoking {@link #invoke()}.
     */
    @SuppressWarnings("serial")
    class ThrowableWrapper extends RuntimeException {

        private final Throwable original;

        public ThrowableWrapper(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }
}
