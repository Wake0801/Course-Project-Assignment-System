package com.example.myproject.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "QuanLiDiem")
public class QuanLiDiem {
    @Id
    @ManyToOne
    @JoinColumn(name = "MaNhom", nullable = false)
    private Nhom nhom;

    @Id
    @ManyToOne
    @JoinColumn(name = "MaDT", nullable = false)
    private DeTai deTai;

    @Id
    @ManyToOne
    @JoinColumn(name = "MaLoaiBaoCao", nullable = false)
    private LoaiBaoCao loaiBaoCao;

    @Column(name = "NgayBaoCao", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayBaoCao;

    @Column(name = "Diem")
    private Float diem;

    @Column(name = "HeSoDiem")
    private Float heSoDiem;

}