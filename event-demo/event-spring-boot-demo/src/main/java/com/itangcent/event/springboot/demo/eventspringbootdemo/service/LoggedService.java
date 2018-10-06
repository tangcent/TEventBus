package com.itangcent.event.springboot.demo.eventspringbootdemo.service;

import com.itangcent.event.annotation.Subscribe;
import org.springframework.stereotype.Service;

@Service
public class LoggedService {

    @Subscribe(topic = "login")
    public void onLogin(String userName) {
        System.out.println(userName + " login");
    }

    @Subscribe(topic = "hi")
    public void onHi(String message) {
        System.out.println(message);
    }

}
