package com.example.myproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {
    @Id
    @Column(name = "MaTK", length = 10)
    @NotBlank(message = "Mã tài khoản không được để trống")
    private String maTK;

    @Column(name = "Username", nullable = false, length = 50, unique = true)
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @Column(name = "Password", nullable = false, length = 255)
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @Column(name = "LoaiTK", nullable = false, length = 50)
    @NotBlank(message = "Loại tài khoản không được để trống")
    private String loaiTK;

    @ManyToOne
    @JoinColumn(name = "MaQuyen", nullable = false)
    @NotNull(message = "Quyền không được để trống")
    private Quyen quyen;

}