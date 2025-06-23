package com.example.myproject.repository;

import com.example.myproject.entity.LopTinChi;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LopTinChiRepository extends JpaRepository<LopTinChi, String> {
    @Query("SELECT l FROM LopTinChi l " +
           "WHERE LOWER(l.maLopTC) LIKE %:keyword% " +
           "OR LOWER(l.monHoc.maMon) LIKE %:keyword% " +
           "OR LOWER(l.giangVien.maGV) LIKE %:keyword%")
    Page<LopTinChi> search(String keyword, Pageable pageable);

    @Query("SELECT MAX(l.maLopTC) FROM LopTinChi l WHERE l.maLopTC LIKE 'LTC%'")
    String findMaxMaLopTC();

    @Query("SELECT l FROM LopTinChi l " +
              "WHERE " +
              "(:keyword IS NULL OR :keyword = '' OR LOWER(l.maLopTC) LIKE %:keyword% " +
              "OR LOWER(l.monHoc.maMon) LIKE %:keyword% " +
              "OR LOWER(l.giangVien.maGV) LIKE %:keyword%) " +
              "AND (:hocKi IS NULL OR l.hocKi = :hocKi) " +
              "AND (:maGV IS NULL OR :maGV = '' OR l.giangVien.maGV = :maGV) " +
              "AND (:trangThai IS NULL OR l.trangThai = :trangThai)")
       Page<LopTinChi> searchAdvanced(
              @Param("keyword") String keyword,
              @Param("hocKi") Integer hocKi,
              @Param("maGV") String maGV,
              @Param("trangThai") Boolean trangThai,
              Pageable pageable
       );

    
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
