package com.example.myproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "NhanVienPKT")
public class NhanVienPKT {
    @Id
    @Column(name = "MaNV", length = 10)
    private String maNV;

    @Column(name = "Ho", nullable = false, length = 50)
    private String ho;

    @Column(name = "Ten", nullable = false, length = 50)
    private String ten;

    @Column(name = "SoDT", length = 15)
    private String soDT;

    @Column(name = "NgaySinh", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate ngaySinh;

    @ManyToOne
    @JoinColumn(name = "MaTK", nullable = false)
    private TaiKhoan taiKhoan;

}