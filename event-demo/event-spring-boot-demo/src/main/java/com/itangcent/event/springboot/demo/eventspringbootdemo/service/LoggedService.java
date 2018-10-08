package com.itangcent.event.springboot.demo.eventspringbootdemo.service;

import com.itangcent.event.annotation.Subscribe;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class LoggedService {

    @Subscribe(topic = "login")
    public void onLogin(String userName) {
        System.out.println(userName + " login success!");
    }

    @Subscribe(topic = "hi", on = "localEventBus")
    public void onHi(String userName) {
        System.out.println("hi," + userName);
    }

    @Subscribe(topic = "log", on = "redisEventBus")
    public void onLog(String log) {
        System.out.println(MessageFormat.format("[{0}]\t{1}",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()),
                log));
    }
}
