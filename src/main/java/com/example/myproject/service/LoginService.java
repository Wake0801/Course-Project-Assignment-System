package com.example.myproject.service;
import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;
    public TaiKhoan login(String username, String password) {
        // 1. Tìm tài khoản theo username
        Optional<TaiKhoan> taiKhoanOpt = loginRepository.findByUsername(username);
        if (taiKhoanOpt.isEmpty()) {
            System.out.println("Không tìm thấy user");
            return null; // Username không tồn tại
        }

        TaiKhoan taiKhoan = taiKhoanOpt.get();

        // 2. So sánh mật khẩu trực tiếp
        if (!password.equals(taiKhoan.getPassword())) {
            System.out.println("Sai mật khẩu");
            return null; // Mật khẩu không đúng
        }
        System.out.println("Đăng nhập thành công!");
        // 3. Đăng nhập thành công
        return taiKhoan;
    }
}
