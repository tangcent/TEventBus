package com.itangcent.event.subscriber;

public interface DelegateSubscriber extends Subscriber {
    Subscriber getDelegate();
}
