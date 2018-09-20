package com.itangcent.event;

public interface EventHandle<E> {

    /**
     * 处理事件
     **/
    void handle(E event);
}
