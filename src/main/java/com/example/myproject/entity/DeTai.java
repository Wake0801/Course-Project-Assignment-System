package com.example.myproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "DeTai")
public class DeTai {
    @Id
    @Column(name = "MaDT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maDT;

    @Column(name = "TenDT", nullable = false, length = 255)
    private String tenDT;

    @Column(name = "MoTa", nullable = false, length = 500)
    private String moTa;

    @Column(name = "NgayBatDau", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate ngayBatDau;

    @ManyToOne
    @JoinColumn(name = "MaLopTC", nullable = false)
    private LopTinChi lopTinChi;
    @ManyToOne
    @JoinColumn(name = "MaNhom")
    private Nhom nhom;
}