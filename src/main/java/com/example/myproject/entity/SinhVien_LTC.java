package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVien_LTC")
public class SinhVien_LTC {
    @Id
    @ManyToOne
    @JoinColumn(name = "MaSV", nullable = false)
    private SinhVien sinhVien;

    @Id
    @ManyToOne
    @JoinColumn(name = "MaLTC", nullable = false)
    private LopTinChi lopTinChi;

}