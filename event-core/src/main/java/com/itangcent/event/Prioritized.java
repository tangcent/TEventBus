package com.itangcent.event;

public interface Prioritized<E> extends Comparable<Prioritized<E>> {
    int getPriority();

    @Override
    default int compareTo(Prioritized<E> o){
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
