package com.example.myproject.config;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.NhanVienPKT;
import com.example.myproject.entity.SinhVien;
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        try{
            HttpSession session = request.getSession();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Lưu thông tin cơ bản
            session.setAttribute("maTK", userDetails.getTaiKhoan().getMaTK());
            session.setAttribute("username", userDetails.getUsername());
            session.setAttribute("role", userDetails.getTaiKhoan().getLoaiTK());
            session.setAttribute("maQuyen", userDetails.getTaiKhoan().getQuyen());

            // Lưu thông tin chi tiết theo từng loại tài khoản
            switch (userDetails.getTaiKhoan().getLoaiTK()) {
                case "SINH_VIEN":
                    SinhVien sinhVien = (SinhVien) userDetails.getUserDetails();
                    session.setAttribute("maSV", sinhVien.getMaSV());
                    session.setAttribute("hoTen", sinhVien.getHo() + " " + sinhVien.getTen());
                    session.setAttribute("email", sinhVien.getEmail());
                    session.setAttribute("maLop", sinhVien.getLop().getMaLop());
                    // Thêm các thông tin khác nếu cần
                    break;
                    
                case "GIANG_VIEN":
                    GiangVien giangVien = (GiangVien) userDetails.getUserDetails();
                    session.setAttribute("maGV", giangVien.getMaGV());
                    session.setAttribute("hoTen", giangVien.getHo() + " " + giangVien.getTen());
                    session.setAttribute("email", giangVien.getEmail());
                    // Thêm các thông tin khác nếu cần
                    break;
                    
                case "NHAN_VIEN":
                    NhanVienPKT nhanVien = (NhanVienPKT) userDetails.getUserDetails();
                    session.setAttribute("maNV", nhanVien.getMaNV());
                    session.setAttribute("hoTen", nhanVien.getHo() + " " + nhanVien.getTen());
                    // Thêm các thông tin khác nếu cần
                    break;
            }

            // Redirect đến trang phù hợp
            String targetUrl = determineTargetUrl(authentication);
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            // Xử lý lỗi và chuyển hướng đến trang mặc định
            System.err.println("Lỗi xử lý đăng nhập: " + e.getMessage());
            redirectStrategy.sendRedirect(request, response, "/login") ;
        }}

    protected String determineTargetUrl(final Authentication authentication) {
        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("SINH_VIEN", "/client/public/home"); // Bỏ tiền tố ROLE_
        roleTargetUrlMap.put("GIANG_VIEN", "/client/public/home");
        roleTargetUrlMap.put("NHAN_VIEN", "/admin/index");
        roleTargetUrlMap.put("ADMIN", "/admin/index");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            // Lấy role không có tiền tố ROLE_
            String rawRole = authority.getAuthority().replace("ROLE_", "");
            if (roleTargetUrlMap.containsKey(rawRole)) {
                return roleTargetUrlMap.get(rawRole);
            }
        }

        // Thêm log để debug
        System.err.println("Không tìm thấy URL cho roles: " + authorities);
        return "/default-home"; // Trang dự phòng thay vì throw exception
    }
}
