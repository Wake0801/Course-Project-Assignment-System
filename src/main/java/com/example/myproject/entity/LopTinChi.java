package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "LopTinChi")
public class LopTinChi {
    @Id
    @Column(name = "MaLopTC", length = 10)
    private String maLopTC;

    @Column(name = "MaMon", length = 10, nullable = false)
    private String maMon;

    @Column(name = "MaGV", length = 10, nullable = false)
    private String maGV;

    @Column(name = "SoLuongToiDa")
    private Integer soLuongToiDa;

    @Column(name = "SoLuongToiThieu", nullable = false)
    private int soLuongToiThieu;

    @Column(name = "HocKi", nullable = false)
    private int hocKi;

    @Column(name = "NienKhoa", nullable = false, length = 20)
    private String nienKhoa;

    @Column(name = "TrangThai", nullable = false)
    private boolean trangThai;

    @Column(name = "NgayMoLop", nullable = false)
    private LocalDateTime ngayLap;

    // Lombok @Getter/@Setter sẽ tự sinh getTrangThai/setTrangThai
    // Nếu không dùng Lombok, thêm thủ công:
    // public boolean getTrangThai() { return trangThai; }
    // public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
}