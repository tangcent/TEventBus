package com.itangcent.event.springboot.demo.eventspringbootdemo.service;

import com.itangcent.event.annotation.Publish;
import com.itangcent.event.annotation.Stage;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Publish(topic = "login", event = "#name")
    public boolean login(String name) {
        //login
        return true;
    }

    @Publish(event = "#name+','+#message", to = "redisEventBus", topic = "log", stage = Stage.BEFORE)
    @Publish(event = "#name", to = "localEventBus", topic = "hi", stage = Stage.AFTER)
    public void hi(String name, String message) {
        //some thing
    }
}
