package com.example.myproject.dto;

import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.SinhVienNhom;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class NhomDTO {
    private int maNhom;
    private String tenNhom;
    private int soLuongTVToiDa;
    private LocalDate ngayLapNhom;
    private LocalDate ngayDongDangKyNhom;
    private List<SinhVienNhom> thanhVien;
    private long soThanhVienHienTai;
    private String maLopTC;
    private String tenMonHoc;

    public NhomDTO(Nhom nhom, List<SinhVienNhom> thanhVien) {
        this.maNhom = nhom.getMaNhom();
        this.tenNhom = nhom.getTenNhom();
        this.soLuongTVToiDa = nhom.getSoLuongTVToiDa();
        this.ngayLapNhom = nhom.getNgayLapNhom();
        this.ngayDongDangKyNhom = nhom.getNgayDongDangKyNhom();
        this.thanhVien = thanhVien;
        this.soThanhVienHienTai = thanhVien.stream().filter(sv -> sv.getNgayRoiNhom() == null).count();
        this.maLopTC = nhom.getLopTinChi().getMaLopTC();
        this.tenMonHoc = nhom.getLopTinChi().getMonHoc().getTenMon();
    }
}