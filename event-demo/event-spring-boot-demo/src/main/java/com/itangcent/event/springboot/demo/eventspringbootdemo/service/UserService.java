package com.itangcent.event.springboot.demo.eventspringbootdemo.service;

import com.itangcent.event.annotation.Publish;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Publish(topic = "login")
    public String login(String name) {
        return name;
    }

    @Publish(event = "#name+','+#message", topic = "hi")
    public void hi(String name, String message) {
    }
}
