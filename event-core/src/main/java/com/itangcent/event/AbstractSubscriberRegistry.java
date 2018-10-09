package com.itangcent.event;

import com.itangcent.event.annotation.Subscribe;
import com.itangcent.event.reflect.TypeToken;
import com.itangcent.event.subscriber.DelegateMethodSubscriber;
import com.itangcent.event.subscriber.Subscriber;
import com.itangcent.event.utils.*;
import com.itangcent.event.utils.Collections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public abstract class AbstractSubscriberRegistry implements SubscriberRegistry {
    /**
     * A thread-safe cache that contains the mapping from each class to all methods in that class and
     * all super-classes, that are annotated with {@code @Subscribe}. The cache is shared across all
     * instances of this class; this greatly improves performance if multiple EventBus instances are
     * created and objects of the same class are registered on all of them.
     */
    private static final Map<Class<?>, List<SubscriberMethod>> subscriberMethodsCache =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers =
            Collections.newConcurrentMap();

    private static List<SubscriberMethod> getAnnotatedMethodsNotCached(Class<?> clazz) {
        Set<? extends Class<?>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
        List<SubscriberMethod> subscribers = new ArrayList<>();
        for (Class<?> supertype : supertypes) {
            for (Method method : supertype.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
                    // TODO:check generic parameter type
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Assert.isTrue(
                            EventUtils.isSuitableMethod(method),
                            "Method %s has @Subscribe annotation but has %s parameters."
                                    + "Subscriber methods must have exactly 1 parameter or 2 parameter with the second String.",
                            method,
                            parameterTypes.length);
                    SubscriberMethod subscriber = new SubscriberMethod();
                    subscriber.setMethod(method);
                    subscriber.setEventType(parameterTypes[0]);
                    subscriber.setSubscribe(AnnotationUtils.findAnnotation(method, Subscribe.class));
                    subscribers.add(subscriber);
                }
            }
        }
        return subscribers;
    }

    @Override
    public void register(Object subscriber) {

        Map<Class<?>, Collection<Subscriber>> listenerMethods = findAllSubscribers(subscriber);

        for (Map.Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> eventMethodsInListener = entry.getValue();

            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);

            if (eventSubscribers == null) {
                CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
                eventSubscribers = ObjectUtils.firstNonNull(subscribers.putIfAbsent(eventType, newSet), newSet);
            }

            eventSubscribers.addAll(eventMethodsInListener);

            for (Subscriber ssb : eventMethodsInListener) {
                onRegisterSubscriber(ssb);
            }
        }
    }

    @Override
    public void unregister(Object subscriber) {
        Map<Class<?>, Collection<Subscriber>> listenerMethods = findAllSubscribers(subscriber);

        for (Map.Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> listenerMethodsForType = entry.getValue();

            CopyOnWriteArraySet<Subscriber> currentSubscribers = subscribers.get(eventType);
            if (currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType)) {
                // if removeAll returns true, all we really know is that at least one subscriber was
                // removed... however, barring something very strange we can assume that if at least one
                // subscriber was removed, all subscribers on listener for that event type were... after
                // all, the definition of subscribers on a particular class is totally static
                throw new IllegalArgumentException(
                        "missing event subscriber for an annotated method. Is " + subscriber + " registered?");
            }

            for (Subscriber ssb : listenerMethodsForType) {
                onUnRegisterSubscriber(ssb);
            }
            // don't try to remove the set if it's empty; that can't be done safely without a lock
            // anyway, if the set is empty it'll just be wrapping an array of length 0
        }
    }

    private SubscribeListener subscribeListener;

    @Override
    public void listen(SubscribeListener subscribeListener) {
        if (this.subscribeListener == null) {
            this.subscribeListener = subscribeListener;
        } else {
            this.subscribeListener = SubscribeListener.union(this.subscribeListener, subscribeListener);
        }
    }

    protected void onRegisterSubscriber(Subscriber subscriber) {
        if (subscribeListener != null) {
            subscribeListener.onRegisterSubscriber(subscriber);
        }
    }

    protected void onUnRegisterSubscriber(Subscriber subscriber) {
        if (subscribeListener != null) {
            subscribeListener.onUnRegisterSubscriber(subscriber);
        }
    }

    @Override
    public void findAllSubscribers(Consumer<Subscriber> subscriberConsumer) {
        for (CopyOnWriteArraySet<Subscriber> subscriberSet : subscribers.values()) {
            for (Subscriber subscriber : subscriberSet) {
                subscriberConsumer.accept(subscriber);
            }
        }
    }

    @Override
    public void findSubscribers(EventBus eventBus, Object event, Consumer<Subscriber> subscriberConsumer) {

        Collection<Class<?>> eventTypes = flattenHierarchy(event.getClass());

        for (Class<?> eventType : eventTypes) {
            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);
            if (eventSubscribers != null) {
                for (Subscriber eventSubscriber : eventSubscribers) {
                    subscriberConsumer.accept(eventSubscriber);
                }
            }
        }

    }

    private Map<Class<?>, Collection<Subscriber>> findAllSubscribers(Object subscriber) {
        Map<Class<?>, Collection<Subscriber>> methodsInListener = Collections.newHashMap();
        Class<?> clazz = subscriber.getClass();
        for (SubscriberMethod subscriberMethod : getAnnotatedMethods(clazz)) {
            Class eventType = subscriberMethod.getEventType();
            Collection<Subscriber> subscribers = methodsInListener.computeIfAbsent(eventType, e -> Collections.newArrayList());
            subscribers.add(buildSubscriber(subscriber, subscriberMethod));
        }
        return methodsInListener;
    }

    private List<SubscriberMethod> getAnnotatedMethods(Class<?> clazz) {
        return subscriberMethodsCache.computeIfAbsent(clazz, AbstractSubscriberRegistry::getAnnotatedMethodsNotCached);
    }

    /**
     * Global cache of classes to their flattened hierarchy of supertypes.
     */
    private static final Map<Class<?>, Collection<Class<?>>> flattenHierarchyCache =
            Collections.newConcurrentMap();

    /**
     * Flattens a class's type hierarchy into a set of {@code Class} objects including all
     * superclasses (transitively) and all interfaces implemented by these superclasses.
     */
    @SuppressWarnings("unchecked")
    static Collection<Class<?>> flattenHierarchy(Class<?> concreteClass) {
        Collection<Class<?>> collection = flattenHierarchyCache.get(concreteClass);
        if (collection == null) {
            synchronized (AbstractSubscriberRegistry.class) {
                collection = flattenHierarchyCache.computeIfAbsent(concreteClass, cls -> (Set<Class<?>>) TypeToken.of(concreteClass).getTypes().rawTypes());
            }
        }
        return collection;
    }

    protected Subscriber buildSubscriber(Object subscriber, SubscriberMethod subscriberMethod) {
        return new DelegateMethodSubscriber(subscriber, subscriberMethod.getMethod(), subscriberMethod.getEventType());
    }

}
