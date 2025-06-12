package com.example.myproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNewPasswordEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Cấp lại mật khẩu");
        message.setText("Mật khẩu mới của bạn là: " + newPassword + "\nVui lòng đăng nhập và đổi mật khẩu.");

        try {
            mailSender.send(message);
            System.out.println("Email gửi thành công");
        } catch (Exception e) {
            System.out.println("Lỗi gửi email: " + e.getMessage());
            throw e;
        }
    }
}

