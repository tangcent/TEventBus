package com.itangcent.event.spring.core;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CompletedApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private volatile boolean init = false;

    private Queue<Runnable> refreshTasks = new LinkedBlockingQueue<>();

    public void addRefreshTasks(Runnable refreshTask) {
        if (init) {
            refreshTask.run();
        } else {
            this.refreshTasks.add(refreshTask);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (init)
            return;
        init = true;
        while (!refreshTasks.isEmpty()) {
            refreshTasks.poll().run();
        }
    }
}
