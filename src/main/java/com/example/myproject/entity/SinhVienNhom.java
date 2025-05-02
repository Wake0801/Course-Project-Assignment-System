package com.example.myproject.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVienNhom")
public class SinhVienNhom {
    @Id
    @ManyToOne
    @JoinColumn(name = "MaSV", nullable = false)
    private SinhVien sinhVien;

    @Id
    @ManyToOne
    @JoinColumn(name = "MaNhom", nullable = false)
    private Nhom nhom;

    @Column(name = "NgayGiaNhap", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayGiaNhap;

    @Column(name = "NgayRoiNhom")
    @Temporal(TemporalType.DATE)
    private Date ngayRoiNhom;

}