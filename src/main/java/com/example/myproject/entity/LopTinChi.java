package com.example.myproject.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    
    @Transient
    private String tenLopTC;

    @ManyToOne
    @JoinColumn(name = "MaMon", nullable = false)
    private MonHoc monHoc;

    @ManyToOne
    @JoinColumn(name = "MaGV", nullable = false)
    private GiangVien giangVien;
    
    @Column(name="NgayMoLop")
    private LocalDate ngayMoLop;

    @Column(name = "SoLuongToiThieu", nullable = false)
    private int soLuongToiThieu;

    @Column(name = "HocKi", nullable = false)
    private int hocKi;

    @Column(name = "TrangThai", nullable = false)
    private boolean trangThai;

    @Column(name = "SoLuongToiDa")
    private Integer soLuongToiDa;
}