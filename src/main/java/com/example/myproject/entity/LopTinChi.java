package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "LopTinChi")
public class LopTinChi {
    @Id
    @Column(name = "MaLopTC", length = 10)
    private String maLopTC;

    @Column(name = "SoLuongSinhVien", nullable = false)
    private int soLuongSinhVien;

    @ManyToOne
    @JoinColumn(name = "MaMon", nullable = false)
    private MonHoc monHoc;

    @ManyToOne
    @JoinColumn(name = "MaGV", nullable = false)
    private GiangVien giangVien;

}