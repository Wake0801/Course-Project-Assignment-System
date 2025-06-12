package com.example.myproject.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.myproject.entity.TaiKhoan;

public class CustomUserDetails implements UserDetails {

    private TaiKhoan taiKhoan;
    private Object userDetails; // Thông tin chi tiết user (SinhVien, GiangVien, NhanVienPKT)

    public CustomUserDetails(TaiKhoan taiKhoan, Object userDetails) {
        this.taiKhoan = taiKhoan;
        this.userDetails = userDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + taiKhoan.getQuyen().getTenQuyen(); // ví dụ ROLE_SINH_VIEN
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return taiKhoan.getPassword();
    }

    @Override
    public String getUsername() {
        return taiKhoan.getUsername();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }
    public Object getUserDetails() {
        return userDetails;
    }
}

