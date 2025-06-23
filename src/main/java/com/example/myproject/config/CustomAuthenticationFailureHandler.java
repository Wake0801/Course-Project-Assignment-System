package com.example.myproject.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        String errorMessage;
        
        if (exception.getMessage().contains("Username phải là email hợp lệ")) {
            errorMessage = "Email không đúng định dạng";
        } else if (exception.getMessage().contains("Không tìm thấy user")) {
            errorMessage = "Email không tồn tại trong hệ thống";
        } else {
            errorMessage = "Sai email hoặc mật khẩu";
        }
        
        session.setAttribute("loginError", errorMessage);
        response.sendRedirect("/login?error=true");
    }
}