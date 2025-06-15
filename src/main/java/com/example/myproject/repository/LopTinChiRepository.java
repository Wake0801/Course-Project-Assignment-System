package com.example.myproject.repository;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.LopTinChi;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LopTinChiRepository extends JpaRepository<LopTinChi, String> {

    Optional<LopTinChi> findByMaLopTC(String maLopTC);
    // Lấy các lớp tín chỉ mà sinh viên đang học
    @Query("SELECT DISTINCT ltc FROM LopTinChi ltc " +
           "JOIN SinhVien_LTC svltc ON svltc.lopTinChi.maLopTC = ltc.maLopTC " +
           "WHERE svltc.sinhVien.maSV = :maSV")
    List<LopTinChi> findBySinhVien(@Param("maSV") String maSV);
    
    // Lấy các lớp tín chỉ mà giảng viên đang dạy
    @Query("SELECT ltc FROM LopTinChi ltc WHERE ltc.giangVien.maGV = :maGV")
    List<LopTinChi> findByGiangVien(@Param("maGV") String maGV);
} 