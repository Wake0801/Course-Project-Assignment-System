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
import com.example.myproject.entity.TaiKhoan;
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                    HttpServletResponse response,
                                    Authentication authentication) throws IOException {
    try {
            HttpSession session = request.getSession();
            
            if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
                throw new IllegalStateException("Principal không phải CustomUserDetails");
            }
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            if (userDetails.getTaiKhoan() == null) {
                throw new IllegalStateException("Đối tượng TaiKhoan là null");
            }
            
            TaiKhoan taiKhoan = userDetails.getTaiKhoan();
            
            // Lưu thông tin cơ bản
            session.setAttribute("maTK", taiKhoan.getMaTK());
            session.setAttribute("username", userDetails.getUsername());
            session.setAttribute("role", taiKhoan.getQuyen().getTenQuyen());
            
            if (taiKhoan.getQuyen() == null) {
                throw new IllegalStateException("Đối tượng Quyen là null");
            }
            
            // Chỉ xử lý userDetails nếu không phải ADMIN
            if (!"ADMIN".equals(taiKhoan.getQuyen().getTenQuyen())) {
                Object userDetailObj = userDetails.getUserDetails();
                if (userDetailObj == null) {
                    throw new IllegalStateException("UserDetails là null");
                }
                
                switch (taiKhoan.getQuyen().getTenQuyen()) {
                    case "SINH_VIEN":
                        if (!(userDetailObj instanceof SinhVien)) {
                            throw new ClassCastException("Kiểu userDetails không phải SinhVien");
                        }
                        SinhVien sinhVien = (SinhVien) userDetailObj;
                        session.setAttribute("maSV", sinhVien.getMaSV());
                        session.setAttribute("hoTen", sinhVien.getHo() + " " + sinhVien.getTen());
                        session.setAttribute("email", sinhVien.getEmail());
                        session.setAttribute("maLop", sinhVien.getLop().getMaLop());
                        break;
                        
                    case "GIANG_VIEN":
                        if (!(userDetailObj instanceof GiangVien)) {
                            throw new ClassCastException("Kiểu userDetails không phải GiangVien");
                        }
                        GiangVien giangVien = (GiangVien) userDetailObj;
                        session.setAttribute("maGV", giangVien.getMaGV());
                        session.setAttribute("hoTen", giangVien.getHo() + " " + giangVien.getTen());
                        session.setAttribute("email", giangVien.getEmail());
                        break;
                        
                    case "NHAN_VIEN":
                        if (!(userDetailObj instanceof NhanVienPKT)) {
                            throw new ClassCastException("Kiểu userDetails không phải NhanVien");
                        }
                        NhanVienPKT nhanVien = (NhanVienPKT) userDetailObj;
                        session.setAttribute("maNV", nhanVien.getMaNV());
                        session.setAttribute("hoTen", nhanVien.getHo() + " " + nhanVien.getTen());
                        break;
                }
            }

            // Redirect đến trang phù hợp
            if (!response.isCommitted()) {
                String targetUrl = determineTargetUrl(authentication);
                redirectStrategy.sendRedirect(request, response, targetUrl);
            }
        } catch (Exception e) {
            System.out.println("Lỗi xử lí đăng nhập "+ e.getMessage());
            if (!response.isCommitted()) {
                response.sendRedirect("/login?error=system_error");
            }
        }
    }

    protected String determineTargetUrl(final Authentication authentication) {
        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("SINH_VIEN", "/client/public/home"); 
        roleTargetUrlMap.put("GIANG_VIEN", "/client/public/home");
        roleTargetUrlMap.put("NHAN_VIEN", "/nvpkt/thong-ke/diem");
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
