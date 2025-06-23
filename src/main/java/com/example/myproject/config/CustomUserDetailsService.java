package com.example.myproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.repository.GiangVienRepository;
import com.example.myproject.repository.NhanVienPKTRepository;
import com.example.myproject.repository.SinhVienRepository;
import com.example.myproject.repository.TaiKhoanRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    
    @Autowired
    private SinhVienRepository sinhVienRepository;
    
    @Autowired
    private GiangVienRepository giangVienRepository;
    
    @Autowired
    private NhanVienPKTRepository nhanVienPKTRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!isValidEmail(username)) {
            throw new UsernameNotFoundException("Username phải là email hợp lệ");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
        
        Object userDetails = null;
        
        switch (taiKhoan.getQuyen().getTenQuyen()) {
            case "SINH_VIEN":
                userDetails = sinhVienRepository.findByTaiKhoan_MaTK(taiKhoan.getMaTK())
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy sinh viên"));
                break;
            case "GIANG_VIEN":
                userDetails = giangVienRepository.findByTaiKhoan_MaTK(taiKhoan.getMaTK())
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy giảng viên"));
                break;
            case "NHAN_VIEN":
                userDetails = nhanVienPKTRepository.findByTaiKhoan_MaTK(taiKhoan.getMaTK())
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhân viên"));
                break;
            case "ADMIN":
                // Không cần lấy thông tin chi tiết cho ADMIN
                userDetails = null;
                break;
            default:
                throw new UsernameNotFoundException("Loại quyền không hợp lệ");
        }
        
        return new CustomUserDetails(taiKhoan, userDetails);
    }
    private boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    return email != null && email.matches(emailRegex);
}
}

