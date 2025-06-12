package com.example.myproject.repository;

import com.example.myproject.entity.LopTinChi;
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
           "OR LOWER(l.maMon) LIKE %:keyword% " +
           "OR LOWER(l.maGV) LIKE %:keyword%")
    Page<LopTinChi> search(String keyword, Pageable pageable);

    @Query("SELECT MAX(l.maLopTC) FROM LopTinChi l WHERE l.maLopTC LIKE 'LTC%'")
    String findMaxMaLopTC();

    @Query("SELECT l FROM LopTinChi l " +
           "WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(l.maLopTC) LIKE %:keyword% OR LOWER(l.maMon) LIKE %:keyword% OR LOWER(l.maGV) LIKE %:keyword%) " +
           "AND (:nienKhoa IS NULL OR :nienKhoa = '' OR l.nienKhoa = :nienKhoa) " +
           "AND (:hocKi IS NULL OR l.hocKi = :hocKi) " +
           "AND (:maGV IS NULL OR :maGV = '' OR l.maGV = :maGV) " +
           "AND (:trangThai IS NULL OR l.trangThai = :trangThai)")
    Page<LopTinChi> searchAdvanced(
            @Param("keyword") String keyword,
            @Param("nienKhoa") String nienKhoa,
            @Param("hocKi") Integer hocKi,
            @Param("maGV") String maGV,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable
    );
}
