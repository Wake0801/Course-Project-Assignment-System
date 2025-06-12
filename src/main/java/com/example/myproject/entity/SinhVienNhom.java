package com.example.myproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.myproject.compositeKey.SinhVienNhomId;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVienNhom")
public class SinhVienNhom {
    @EmbeddedId
    private SinhVienNhomId sinhVienNhomId;

    @ManyToOne
    @JoinColumn(name = "MaSV", referencedColumnName = "MaSV", insertable = false, updatable = false)
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "MaNhom", referencedColumnName = "MaNhom", insertable = false, updatable = false)
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