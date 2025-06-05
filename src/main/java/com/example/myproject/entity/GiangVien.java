package com.example.myproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "GiangVien")
public class GiangVien {
    @Id
    @Column(name = "MaGV", length = 10)
    private String maGV;

    @Column(name = "Ho", nullable = false, length = 50)
    private String ho;

    @Column(name = "Ten", nullable = false, length = 50)
    private String ten;

    @Column(name = "SoDT", length = 15)
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số")
    @Size(min = 10, max = 10, message = "Số điện thoại phải có đúng 10 chữ số")
    private String soDT;

    @Column(name = "NgaySinh", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate ngaySinh;

    @Column(name = "Email", length = 100, unique = true)
    private String email;
    
    @Column(name = "HocVi", length = 50)
    private String hocVi;

    @ManyToOne
    @JoinColumn(name = "MaKhoa", nullable = false)
    private Khoa khoa;

    @ManyToOne
    @JoinColumn(name = "MaTK", nullable = false)
    private TaiKhoan taiKhoan;

}