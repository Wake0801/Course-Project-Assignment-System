package com.example.myproject.entity;

import java.io.Serializable;
import java.util.Objects;

public class SinhVien_LTCPK implements Serializable {
    private String sinhVien;   // MaSV
    private String lopTinChi;  // MaLopTC

    public SinhVien_LTCPK() {}

    public SinhVien_LTCPK(String sinhVien, String lopTinChi) {
        this.sinhVien = sinhVien;
        this.lopTinChi = lopTinChi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SinhVien_LTCPK)) return false;
        SinhVien_LTCPK that = (SinhVien_LTCPK) o;
        return Objects.equals(sinhVien, that.sinhVien) &&
               Objects.equals(lopTinChi, that.lopTinChi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sinhVien, lopTinChi);
    }
}
