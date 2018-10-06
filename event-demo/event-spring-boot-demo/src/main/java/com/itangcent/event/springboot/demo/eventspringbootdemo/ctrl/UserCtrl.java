package com.itangcent.event.springboot.demo.eventspringbootdemo.ctrl;

import com.itangcent.event.springboot.demo.eventspringbootdemo.service.UserService;
import org.springframework.util.Assert;
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
        Assert.isTrue(userService.login(name), "success");
        return "hello:" + name;
    }

    @RequestMapping(value = "/hi/{name}/{message}", method = RequestMethod.GET)
    public String login2(@PathVariable(value = "name") String name, @PathVariable(value = "message") String message) {
        userService.hi(name, message);
        return "hi:" + name + "," + message;
    }
}
