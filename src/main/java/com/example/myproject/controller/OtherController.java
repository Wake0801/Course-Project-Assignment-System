
package com.example.myproject.controller;
import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.repository.LoginRepository;
import com.example.myproject.service.MailService;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/other")
@RequiredArgsConstructor
public class OtherController {
    private String generateRandomPassword() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Autowired
    private LoginRepository loginRepository ;
    @Autowired
    private MailService mailService ;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/forwardPassword")
    public String gfP(Model model){
        model.addAttribute("taiKhoan", new TaiKhoan());
        return "other/forwardPassword";
    }


    @PostMapping("/forwardPassword")
    public String forwardPassword(
            @ModelAttribute("taiKhoan") TaiKhoan taiKhoan,
            RedirectAttributes redirectAttributes
    ) {
        String username = taiKhoan.getUsername();
        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email không được để trống!");
            return "redirect:/other/forwardPassword";
        }

        Optional<TaiKhoan> tkOptional = loginRepository.findByUsername(username.trim());
        if (tkOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại!");
            return "redirect:/other/forwardPassword";
        }

        String newPassword = generateRandomPassword();
        String encoded = passwordEncoder.encode(newPassword);

        TaiKhoan tk = tkOptional.get();
        tk.setPassword(encoded);
        loginRepository.save(tk);

        try {
            System.out.println("Gửi email đến: " + tk.getUsername() + " với mật khẩu: " + newPassword);
            mailService.sendNewPasswordEmail(tk.getUsername(), newPassword);
            redirectAttributes.addFlashAttribute("success", "Mật khẩu mới đã được gửi đến email.");
            return "redirect:/other/login";
        } catch (Exception e) {
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("error", "Không gửi được email: " + e.getMessage());
        }
        return "redirect:/other/forwardPassword";
    }

}
