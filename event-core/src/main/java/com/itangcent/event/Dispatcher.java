package com.itangcent.event;

import java.util.Iterator;

public interface Dispatcher {
    void dispatch(Object event, Iterator<Subscriber> subscribers);
}
