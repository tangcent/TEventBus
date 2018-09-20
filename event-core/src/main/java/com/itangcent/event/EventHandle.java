package com.itangcent.event;

public interface EventHandle<E> {
    void handle(E event);
}
