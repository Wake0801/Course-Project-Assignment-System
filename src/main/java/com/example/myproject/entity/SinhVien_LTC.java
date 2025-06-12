package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVien_LTC")
@IdClass(SinhVien_LTCPK.class)
public class SinhVien_LTC {
    @Id
    @ManyToOne
    @JoinColumn(name = "MaSV", nullable = false)
    private SinhVien sinhVien;

    @Id
    @ManyToOne
    @JoinColumn(name = "MaLopTC", nullable = false)
    private LopTinChi lopTinChi;

}