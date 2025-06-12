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
        TaiKhoan taiKhoan = taiKhoanRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Object userDetails = null;
        
        switch (taiKhoan.getLoaiTK()) {
            case "SINH_VIEN":
                userDetails = sinhVienRepository.findByTaiKhoan_MaTK(taiKhoan.getMaTK())
                    .orElseThrow(() -> new UsernameNotFoundException("Sinh vien not found"));
                break;
            case "GIANG_VIEN":
                userDetails = giangVienRepository.findByTaiKhoan_MaTK(taiKhoan.getMaTK())
                    .orElseThrow(() -> new UsernameNotFoundException("Giang vien not found"));
                break;
            case "NHAN_VIEN":
                userDetails = nhanVienPKTRepository.findByTaiKhoan_MaTK(taiKhoan.getMaTK())
                    .orElseThrow(() -> new UsernameNotFoundException("Nhan vien not found"));
                break;
        }
        
        return new CustomUserDetails(taiKhoan, userDetails);
    }
}

