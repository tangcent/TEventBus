package com.itangcent.event;

import com.itangcent.event.subscriber.DelegateSubscriber;
import com.itangcent.event.subscriber.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorDispatcher extends AbstractDispatcher {

    private ExecutorService executorService;

    private ExecutorDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ExecutorDispatcher(int thread) {
        this(createThreadPool(thread));
    }

    public ExecutorDispatcher() {
        this(createThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    @Override
    public void dispatch(Object event, Subscriber subscriber, SubscriberExceptionHandler subscriberExceptionHandler) {
        executorService.submit(new PrioritizedRunnable(getPriority(subscriber)) {
            @Override
            public void run() {
                dispatchEvents(event, subscriber, subscriberExceptionHandler);
            }
        });
    }

    private int getPriority(Subscriber subscriber) {
        for (; ; ) {
            if (subscriber instanceof Prioritized) {
                return ((Prioritized) subscriber).getPriority();
            }
            if (subscriber instanceof DelegateSubscriber) {
                subscriber = ((DelegateSubscriber) subscriber).getDelegate();
                continue;
            }
            return Thread.NORM_PRIORITY;
        }
    }

    private abstract class PrioritizedRunnable implements Runnable, Prioritized<PrioritizedRunnable> {
        private int priority;

        public PrioritizedRunnable(int priority) {
            this.priority = priority;
        }

        @Override
        public int getPriority() {
            return priority;
        }
    }

    private static ExecutorService createThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>());
    }
}
