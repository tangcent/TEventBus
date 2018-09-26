package com.itangcent.event.springboot.demo.eventspringbootdemo.ctrl;

import com.itangcent.event.springboot.demo.eventspringbootdemo.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserCtrl {

    @Resource
    UserService userService;

    @RequestMapping(value = "/login/{name}", method = RequestMethod.GET)
    public String login(@PathVariable(value = "name") String name) {
        userService.login(name);
        return "hello:" + name;
    }
}