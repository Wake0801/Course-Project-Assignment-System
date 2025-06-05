package com.example.myproject.repository;

import com.example.myproject.entity.MonHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MonHocRepository extends JpaRepository<MonHoc, String> {
    
    // Lấy môn học theo khoa (thông qua lớp tín chỉ → giảng viên)
    @Query("SELECT DISTINCT ltc.monHoc FROM LopTinChi ltc WHERE ltc.giangVien.khoa.maKhoa = :maKhoa")
    java.util.List<MonHoc> findDistinctByLopTinChis_GiangVien_Khoa_MaKhoa(@Param("maKhoa") String maKhoa);
    
    // Lấy môn học theo giảng viên (thông qua lớp tín chỉ)
    @Query("SELECT DISTINCT ltc.monHoc FROM LopTinChi ltc WHERE ltc.giangVien.maGV = :maGV")
    java.util.List<MonHoc> findDistinctByLopTinChis_GiangVien_MaGV(@Param("maGV") String maGV);
} 