package com.example.myproject.repository;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.Nhom;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeTaiRepository extends JpaRepository<DeTai, Integer> {
    
    //Tìm kiếm đơn giản
    @Query("SELECT dt FROM DeTai dt WHERE " +
           "LOWER(dt.tenDT) LIKE %:keyword% OR " +
           "LOWER(dt.moTa) LIKE %:keyword% OR " +
           "LOWER(dt.lopTinChi.giangVien.ten) LIKE %:keyword% OR " +
           "LOWER(dt.lopTinChi.giangVien.ho) LIKE %:keyword%")
    Page<DeTai> search(@Param("keyword") String keyword, Pageable pageable);

    //Tìm kiếm với các bộ lọc
    @Query("SELECT d FROM DeTai d WHERE " +
           "(:maKhoa IS NULL OR d.lopTinChi.giangVien.khoa.maKhoa = :maKhoa) AND " +
           "(:maGV IS NULL OR d.lopTinChi.giangVien.maGV = :maGV) AND " +
           "(COALESCE(:keyword, '') = '' OR " +
           "LOWER(d.tenDT) LIKE %:keyword% OR " +
           "LOWER(d.moTa) LIKE %:keyword% OR " +
           "LOWER(d.lopTinChi.giangVien.ho) LIKE %:keyword% OR " +
           "LOWER(d.lopTinChi.giangVien.ten) LIKE %:keyword%)")
    Page<DeTai> findByFilters(@Param("maKhoa") String maKhoa,
                             @Param("maGV") String maGV,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    // Tìm kiếm nâng cao với LopTinChi và MonHoc
    @Query("SELECT d FROM DeTai d WHERE " +
           "(:maKhoa IS NULL OR :maKhoa = '' OR d.lopTinChi.giangVien.khoa.maKhoa = :maKhoa) AND " +
           "(:maGV IS NULL OR :maGV = '' OR d.lopTinChi.giangVien.maGV = :maGV) AND " +
           "(:maLopTC IS NULL OR :maLopTC = '' OR d.lopTinChi.maLopTC = :maLopTC) AND " +
           "(:maMon IS NULL OR :maMon = '' OR d.lopTinChi.monHoc.maMon = :maMon) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(d.tenDT) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.moTa) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.lopTinChi.giangVien.ho) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.lopTinChi.giangVien.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.lopTinChi.monHoc.tenMon) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<DeTai> findByAdvancedFilters(@Param("maKhoa") String maKhoa,
                                     @Param("maGV") String maGV,
                                     @Param("maLopTC") String maLopTC,
                                     @Param("maMon") String maMon,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);

    List<DeTai> findByLopTinChi_MaLopTCIn(List<String> maLopTCs);
    
    List<DeTai> findByLopTinChi_MaLopTC(String maLopTC);

    @Query("SELECT d.nhom FROM DeTai d WHERE d.maDT = :maDT")
       Nhom findNhomByDeTaiMaDT(@Param("maDT") int maDT);
}