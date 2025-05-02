package com.example.myproject.controller;

import com.example.myproject.dto.TaiKhoanDTO;
import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/other")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("taiKhoan", new TaiKhoan());
        return "other/login"; 
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("taiKhoan") TaiKhoanDTO taiKhoan, 
                        BindingResult result, 
                        Model model) {
        // Kiểm tra lỗi validation từ form (ví dụ: username hoặc password để trống)
        if (result.hasErrors()) {
            model.addAttribute("taiKhoan", taiKhoan);
            return "other/login"; // Trả về form nếu có lỗi
        }

        // Gọi service để kiểm tra đăng nhập
        TaiKhoan loggedInTaiKhoan = loginService.login(taiKhoan.getUsername(), taiKhoan.getPassword());
        
        if (loggedInTaiKhoan == null) {
            // Nếu đăng nhập thất bại, thêm lỗi vào BindingResult
            result.rejectValue("username", "error.username", "Tên đăng nhập hoặc mật khẩu không đúng");
            model.addAttribute("taiKhoan", taiKhoan);
            return "other/login"; // Trả về form với thông báo lỗi
        }
        String loaiTK = loggedInTaiKhoan.getLoaiTK();
        if ("Giảng viên".equalsIgnoreCase(loaiTK) || "Sinh viên".equalsIgnoreCase(loaiTK)) {
            return "client/home";
        } else if ("Quản trị".equalsIgnoreCase(loaiTK)) {
            return "admin/index";
        } else {
            // Nếu loại tài khoản không hợp lệ, có thể trả về lỗi hoặc trang mặc định
            result.rejectValue("username", "error.username", "Loại tài khoản không hợp lệ");
            model.addAttribute("taiKhoan", taiKhoan);
            return "other/login";
        }
    }
}