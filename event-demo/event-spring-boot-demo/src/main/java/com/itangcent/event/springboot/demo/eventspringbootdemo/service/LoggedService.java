package com.itangcent.event.springboot.demo.eventspringbootdemo.service;

import com.itangcent.event.annotation.Subscribe;
import org.springframework.stereotype.Service;

@Service
public class LoggedService {

    @Subscribe
    public void onLogin(String userName) {
        System.out.println(userName + " login");
    }

}
