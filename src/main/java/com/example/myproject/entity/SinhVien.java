package com.example.myproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVien")
public class SinhVien {
    @Id
    @Column(name = "MaSV", length = 10)
    private String maSV;

    @Column(name = "Ho", nullable = false, length = 50)
    private String ho;

    @Column(name = "Ten", nullable = false, length = 50)
    private String ten;

    @Column(name = "GioiTinh", nullable = false)
    private boolean gioiTinh;

    @Column(name = "SoDT", length = 15)
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số")
    @Size(min = 10, max = 10, message = "Số điện thoại phải có đúng 10 chữ số")
    private String soDT;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "NgaySinh", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate ngaySinh;

    @Column(name = "Email", length = 100, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "MaLop", nullable = false)
    private Lop lop;

    @ManyToOne
    @JoinColumn(name = "MaTK", nullable = false)
    private TaiKhoan taiKhoan;


}