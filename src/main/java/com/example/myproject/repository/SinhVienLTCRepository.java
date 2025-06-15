package com.example.myproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.myproject.compositeKey.SinhVienLTCId;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.SinhVien_LTC;

public interface SinhVienLTCRepository extends JpaRepository<SinhVien_LTC, SinhVienLTCId> {
    @Query("SELECT svltc.lopTinChi FROM SinhVien_LTC svltc WHERE svltc.sinhVienLTCId.maSV = :maSV")
    List<LopTinChi> findLopTinChiByMaSV(@Param("maSV") String maSV);
    @Query("SELECT COUNT(svltc) > 0 FROM SinhVien_LTC svltc " +
           "WHERE svltc.sinhVien.maSV = :maSV AND svltc.lopTinChi.maLopTC = :maLopTC")
    boolean existsBySinhVien_MaSVAndLopTinChi_MaLopTC(@Param("maSV") String maSV, @Param("maLopTC") String maLopTC);
    
    // Đếm số sinh viên trong một lớp tín chỉ
    @Query("SELECT COUNT(svltc) FROM SinhVien_LTC svltc WHERE svltc.lopTinChi.maLopTC = :maLopTC")
    int countByLopTinChi_MaLopTC(@Param("maLopTC") String maLopTC);
    @Query("SELECT svltc FROM SinhVien_LTC svltc WHERE svltc.lopTinChi.maLopTC = :maLopTC")
    List<SinhVien_LTC> findByLopTinChi_MaLopTC(@Param("maLopTC") String maLopTC);
}
