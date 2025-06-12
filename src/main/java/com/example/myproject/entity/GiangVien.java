package com.example.myproject.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "GiangVien")
public class GiangVien {
    @Id
    @Column(name = "MaGV", length = 10)
    private String maGV;

    @Column(name = "Ho", nullable = false, length = 50)
    private String ho;

    @Column(name = "Ten", nullable = false, length = 50)
    private String ten;

    @Column(name = "SoDT", length = 15)
    private String soDT;

    @Column(name = "NgaySinh", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "HocVi", length = 50)
    private String hocVi;

    @ManyToOne
    @JoinColumn(name = "MaKhoa", nullable = false)
    private Khoa khoa;

    @ManyToOne
    @JoinColumn(name = "MaTK", nullable = false)
    private TaiKhoan taiKhoan;


    public String getTenGV() {
        return (ho != null ? ho : "") + " " + (ten != null ? ten : "");
    }
}