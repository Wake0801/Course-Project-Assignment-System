package com.example.myproject.entity;

import com.example.myproject.compositeKey.SinhVienLTCId;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVien_LTC")
public class SinhVien_LTC {
    
    @EmbeddedId
    private SinhVienLTCId sinhVienLTCId;
    @ManyToOne
    @JoinColumn(name = "MaSV", referencedColumnName = "MaSV", insertable = false, updatable = false)
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "MaLopTC", referencedColumnName = "MaLopTC", insertable = false, updatable = false)
    private LopTinChi lopTinChi;
}