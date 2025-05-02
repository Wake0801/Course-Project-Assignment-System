package com.example.myproject.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "DeTai")
public class DeTai {
    @Id
    @Column(name = "MaDT", length = 10)
    private String maDT;

    @Column(name = "TenDT", nullable = false, length = 255)
    private String tenDT;

    @Column(name = "MoTa", nullable = false, length = 500)
    private String moTa;

    @Column(name = "NgayBatDau", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @ManyToOne
    @JoinColumn(name = "MaLopTC", nullable = false)
    private LopTinChi lopTinChi;
}