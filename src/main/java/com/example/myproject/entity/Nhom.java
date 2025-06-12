package com.example.myproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Nhom")
public class Nhom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNhom")
    private int maNhom;

    @Column(name = "TenNhom", nullable = false, length = 100)
    private String tenNhom;

    @Column(name = "SoLuongTVToiDa", nullable = false)
    private int soLuongTVToiDa;

    @Transient
    private long soThanhVienHienTai;
}