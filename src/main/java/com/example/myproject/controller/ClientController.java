package com.example.myproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ClientController {

    @GetMapping("/home")
    public String home() {
        return "client/home";
    }

    @GetMapping("/notification")
    public String notification() {
        return "client/notification";
    }

    @GetMapping("/password")
    public String password() {
        return "client/password";
    }

    @GetMapping("/service")
    public String service() {
        return "client/service";
    }

    @GetMapping("/studentgroup")
    public String studentGroup() {
        return "client/studentgroup";
    }

    @GetMapping("/studentlist")
    public String studentList() {
        return "client/studentlist";
    }

    @GetMapping("/test")
    public String test() {
        return "client/test";
    }

    @GetMapping("/topic")
    public String topic() {
        return "client/topic";
    }

    @GetMapping("/userinfo")
    public String userInfo() {
        return "client/userinfo";
    }
}
