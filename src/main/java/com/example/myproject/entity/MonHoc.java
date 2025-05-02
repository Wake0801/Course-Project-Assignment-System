package com.example.myproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "MonHoc")
public class MonHoc {
    @Id
    @Column(name = "MaMon", length = 10)
    private String maMon;

    @Column(name = "TenMon", nullable = false, length = 100)
    private String tenMon;

    @Column(name = "SoTietLT", nullable = false)
    private int soTietLT;

    @Column(name = "SoTietTH", nullable = false)
    private int soTietTH;

}