package com.example.myproject.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "SinhVienNhom")
@IdClass(SinhVienNhom.PK.class)
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

    @Getter
    @Setter
    public static class PK implements Serializable {
        private String sinhVien;
        private Integer nhom;
        // equals & hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK)) return false;
            PK pk = (PK) o;
            return sinhVien.equals(pk.sinhVien) && nhom.equals(pk.nhom);
        }
        @Override
        public int hashCode() {
            return sinhVien.hashCode() + nhom.hashCode();
        }
    }
}