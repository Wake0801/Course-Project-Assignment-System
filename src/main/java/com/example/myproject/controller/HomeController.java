package com.example.myproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("/login") // Phải trùng với loginPage
    public String showLoginForm(
        @RequestParam(value = "error", required = false) String error,
        Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
        }
        return "other/login"; // Trả về template login.html
    }
    @RequestMapping("")
    public String requestMethodName() {
        return "other/login"; 
    }
    
}