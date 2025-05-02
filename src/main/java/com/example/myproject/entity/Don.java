package com.example.myproject.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "Don")
public class Don {
    @Id
    @Column(name = "MaDon", length = 10)
    private String maDon;

    @Column(name = "LyDo", nullable = false, length = 500)
    private String lyDo;

    @Column(name = "BangChung", length = 500)
    private String bangChung;

    @Column(name = "NgayLap", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayLap;

    @Column(name = "TrangThai", nullable = false, length = 50)
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "MaSVLapDon", nullable = false)
    private SinhVien sinhVienLapDon;

    @ManyToOne
    @JoinColumn(name = "MaGVDuyet")
    private GiangVien giangVienDuyet;

    @Column(name = "NgayDuyet")
    @Temporal(TemporalType.DATE)
    private Date ngayDuyet;

}