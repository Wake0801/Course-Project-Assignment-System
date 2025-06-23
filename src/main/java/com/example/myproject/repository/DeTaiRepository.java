package com.example.myproject.repository;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.Nhom;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeTaiRepository extends JpaRepository<DeTai, Integer> {
    Optional<DeTai> findById(Integer maDT);

    @Query("SELECT dt FROM DeTai dt WHERE " +
           "dt.maDT = :keyword OR " +
           "LOWER(dt.tenDT) LIKE %:keyword% OR " +
           "LOWER(dt.moTa) LIKE %:keyword% OR " +
           "LOWER(dt.lopTinChi.giangVien.ten) LIKE %:keyword% OR " +
           "LOWER(dt.lopTinChi.giangVien.ho) LIKE %:keyword%")
    Page<DeTai> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT d FROM DeTai d WHERE " +
           "(:maKhoa IS NULL OR d.lopTinChi.giangVien.khoa.maKhoa = :maKhoa) AND " +
           "(:maGV IS NULL OR d.lopTinChi.giangVien.maGV = :maGV) AND " +
           "(COALESCE(:keyword, '') = '' OR " +
           "d.maDT = :keyword OR " +
           "LOWER(d.tenDT) LIKE %:keyword% OR " +
           "LOWER(d.moTa) LIKE %:keyword% OR " +
           "LOWER(d.lopTinChi.giangVien.ho) LIKE %:keyword% OR " +
           "LOWER(d.lopTinChi.giangVien.ten) LIKE %:keyword%)")
    Page<DeTai> findByFilters(@Param("maKhoa") String maKhoa,
                             @Param("maGV") String maGV,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    List<DeTai> findByLopTinChi_MaLopTCIn(List<String> maLopTCs);
    
    List<DeTai> findByLopTinChi_MaLopTC(String maLopTC);
    
    List<DeTai> findByNhom_MaNhom(int maNhom);
    
    
    @Query("SELECT d.nhom FROM DeTai d WHERE d.maDT = :maDT")
    Nhom findNhomByDeTaiMaDT(@Param("maDT") int maDT);

       @Query("SELECT d FROM DeTai d " +
       "JOIN LoaiBaoCaoDeTai lbc ON lbc.deTai.maDT = d.maDT " +
       "WHERE d.lopTinChi.maLopTC = :maLopTC " +
       "AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao " +
       "AND (:keyword IS NULL OR " +
       "d.maDT = :keyword OR " +
       "LOWER(d.tenDT) LIKE %:keyword% OR " +
       "LOWER(d.moTa) LIKE %:keyword%)")
       List<DeTai> findByLopTinChi_MaLopTCAndLoaiBaoCao(
       @Param("maLopTC") String maLopTC,
       @Param("maLoaiBaoCao") int maLoaiBaoCao,
       @Param("keyword") String keyword
       );

       // Phiên bản có phân trang
       @Query("SELECT d FROM DeTai d " +
              "JOIN LoaiBaoCaoDeTai lbc ON lbc.deTai.maDT = d.maDT " +
              "WHERE d.lopTinChi.maLopTC = :maLopTC " +
              "AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao " +
              "AND (:keyword IS NULL OR " +
              "d.maDT = :keyword OR " +
              "LOWER(d.tenDT) LIKE %:keyword% OR " +
              "LOWER(d.moTa) LIKE %:keyword%)")
       Page<DeTai> findByLopTinChi_MaLopTCAndLoaiBaoCao(
       @Param("maLopTC") String maLopTC,
       @Param("maLoaiBaoCao") int maLoaiBaoCao,
       @Param("keyword") String keyword,
       Pageable pageable
       );
       
       
       @Query("SELECT d FROM DeTai d " +
       "JOIN LoaiBaoCaoDeTai lbc ON lbc.deTai.maDT = d.maDT " +
       "WHERE d.lopTinChi.maLopTC = :maLopTC " +
       "AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao " +
       "AND d.nhom IS NULL")  // Chỉ lấy đề tài chưa có nhóm
       List<DeTai> findByLopTinChi_MaLopTCAndLoaiBaoCao(
       @Param("maLopTC") String maLopTC,
       @Param("maLoaiBaoCao") int maLoaiBaoCao
       );
       
       
       
       @Query("SELECT d FROM DeTai d " +
              "WHERE d.lopTinChi.maLopTC = :maLopTC " +
              "AND (:keyword IS NULL OR " +
              "d.maDT = :keyword OR " +
              "LOWER(d.tenDT) LIKE %:keyword% OR " +
              "LOWER(d.moTa) LIKE %:keyword%)")
       Page<DeTai> findByLopTinChi_MaLopTC(
       @Param("maLopTC") String maLopTC,
       @Param("keyword") String keyword,
       Pageable pageable);
       
       
       
       @Query("SELECT d FROM DeTai d WHERE d.lopTinChi.maLopTC = :maLopTC AND d.nhom IS NULL")
       List<DeTai> findDeTaiChuaCoNhomByLopTC(@Param("maLopTC") String maLopTC);


       @Query("SELECT d FROM DeTai d WHERE d.lopTinChi.maLopTC = :maLopTC AND d.nhom IS NOT NULL")
       List<DeTai> findDeTaiDaCoNhomByLopTC(@Param("maLopTC") String maLopTC);


       @Query("SELECT d FROM DeTai d WHERE d.lopTinChi.maLopTC = :maLopTC AND " +
              "EXISTS (SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.maDT = d.maDT AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao) AND " +
              "d.nhom IS NULL")
       List<DeTai> findDeTaiChuaCoNhomByLopTCAndLoaiBaoCao(
       @Param("maLopTC") String maLopTC, 
       @Param("maLoaiBaoCao") int maLoaiBaoCao
       );

       @Query("SELECT COUNT(d) FROM DeTai d WHERE d.lopTinChi.maLopTC = :maLopTC AND d.nhom IS NULL")
       long countDeTaiChuaCoNhomByLopTC(@Param("maLopTC") String maLopTC);

       @Query("SELECT d FROM DeTai d WHERE d.nhom.maNhom = :maNhom AND " +
              "EXISTS (SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.maDT = d.maDT AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao)")
       Optional<DeTai> findByNhomAndLoaiBaoCao(
       @Param("maNhom") int maNhom,
       @Param("maLoaiBaoCao") int maLoaiBaoCao
       );

       @Query("SELECT d FROM DeTai d WHERE d.lopTinChi.maLopTC = :maLopTC AND " +
              "EXISTS (SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.maDT = d.maDT AND lbc.diem IS NULL)")
       List<DeTai> findDeTaiChuaCoDiemByLopTC(@Param("maLopTC") String maLopTC);
       @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DeTai d WHERE d.nhom.maNhom = :maNhom")
       boolean existsByNhom_MaNhom(@Param("maNhom") int maNhom);
}