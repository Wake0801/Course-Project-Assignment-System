package com.example.myproject.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, 
                               HttpServletResponse response,
                               Authentication authentication) throws IOException {
        
        // Xóa session
        request.getSession().invalidate();
        
        // Redirect về trang chủ với thông báo logout thành công
        String targetUrl = "/login?logout=success";
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}