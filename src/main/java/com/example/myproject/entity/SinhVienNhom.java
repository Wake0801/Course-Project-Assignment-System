package com.example.myproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVienNhom")
public class SinhVienNhom {
    @Id
    @ManyToOne
    @JoinColumn(name = "MaSV", nullable = false)
    private SinhVien sinhVien;

    @Id
    @ManyToOne
    @JoinColumn(name = "MaNhom", nullable = false)
    private Nhom nhom;

    @Column(name = "NgayGiaNhap", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate ngayGiaNhap;

    @Column(name = "NgayRoiNhom")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate ngayRoiNhom;

}