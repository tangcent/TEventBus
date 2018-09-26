package com.itangcent.event.springboot.demo.eventspringbootdemo.service;

import com.itangcent.event.annotation.Publish;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Publish(to = "local")
    public String login(String name) {
        return name;
    }

}