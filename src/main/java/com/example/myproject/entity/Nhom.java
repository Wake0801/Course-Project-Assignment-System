package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Nhom")
public class Nhom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNhom")
    private Integer maNhom;

    @Column(name = "TenNhom", nullable = false, length = 50)
    private String tenNhom;

    @Column(name = "SoLuongTVToiDa", nullable = false)
    private Integer soLuongTVToiDa;

    @Column(name = "MaLopTC", length = 10, nullable = false)
    private String maLopTC;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NgayDongDangKyNhom")
    @Temporal(TemporalType.DATE)
    private Date ngayDongDangKyNhom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NgayLapNhom")
    @Temporal(TemporalType.DATE)
    private Date ngayLapNhom;
}