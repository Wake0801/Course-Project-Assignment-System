package com.example.myproject.repository;

import com.example.myproject.entity.DeTai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeTaiRepository extends JpaRepository<DeTai, String> {
    
    @Query("SELECT dt FROM DeTai dt WHERE " +
           "LOWER(dt.maDT) LIKE %:keyword% OR " +
           "LOWER(dt.tenDT) LIKE %:keyword% OR " +
           "LOWER(dt.moTa) LIKE %:keyword% OR " +
           "LOWER(dt.lopTinChi.giangVien.ten) LIKE %:keyword% OR " +
           "LOWER(dt.lopTinChi.giangVien.ho) LIKE %:keyword%")
    Page<DeTai> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT d FROM DeTai d WHERE " +
           "(:maKhoa IS NULL OR d.lopTinChi.giangVien.khoa.maKhoa = :maKhoa) AND " +
           "(:maGV IS NULL OR d.lopTinChi.giangVien.maGV = :maGV) AND " +
           "(COALESCE(:keyword, '') = '' OR " +
           "LOWER(d.maDT) LIKE %:keyword% OR " +
           "LOWER(d.tenDT) LIKE %:keyword% OR " +
           "LOWER(d.moTa) LIKE %:keyword% OR " +
           "LOWER(d.lopTinChi.giangVien.ho) LIKE %:keyword% OR " +
           "LOWER(d.lopTinChi.giangVien.ten) LIKE %:keyword%)")
    Page<DeTai> findByFilters(@Param("maKhoa") String maKhoa,
                             @Param("maGV") String maGV,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    // Page<DeTai> findByFilters(String filterKhoa, String filterGiangVien, Object object, Object object2,
    //         Pageable pageable);
}