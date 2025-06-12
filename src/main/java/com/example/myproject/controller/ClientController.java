package com.example.myproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ClientController {

    @GetMapping("/public/home")
    public String home() {
        return "client/public/home";
    }

    @GetMapping("/public/notification")
    public String notification() {
        return "client/public/notification";
    }

    @GetMapping("/public/password")
    public String password() {
        return "client/public/password";
    }

    @GetMapping("/gv/service")
    public String service() {
        return "client/service";
    }

    @GetMapping("/gv/studentgroup")
    public String studentGroup() {
        return "client/gv/studentgroup";
    }

    @GetMapping("/gv/studentlist")
    public String studentList() {
        return "client/gv/studentlist";
    }



    @GetMapping("/public/topic")
    public String topic() {
        return "client/public/topic";
    }

    @GetMapping("/public/userinfo")
    public String userInfo() {
        return "client/public/userinfo";
    }
}
