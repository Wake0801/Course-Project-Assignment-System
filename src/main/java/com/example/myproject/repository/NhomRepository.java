package com.example.myproject.repository;

import com.example.myproject.compositeKey.SinhVienNhomId;
import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.SinhVienNhom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NhomRepository extends JpaRepository<Nhom, Integer> {
    Optional<Nhom> findById(Integer maNhom);

    @Query("SELECT n FROM Nhom n WHERE LOWER(n.tenNhom) LIKE %:keyword%")
    Page<Nhom> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT n.*, " +
           "(SELECT COUNT(svn.MaSV) FROM SinhVienNhom svn " +
           "WHERE svn.MaNhom = n.MaNhom AND svn.NgayRoiNhom IS NULL) as memberCount " +
           "FROM Nhom n", nativeQuery = true)
    Page<Nhom> findAllWithMemberCount(Pageable pageable);

    @Query("SELECT COUNT(svn) FROM SinhVienNhom svn WHERE svn.nhom.maNhom = :maNhom AND svn.ngayRoiNhom IS NULL")
    long countMembersByMaNhom(@Param("maNhom") int maNhom);
     List<Nhom> findByLopTinChi_MaLopTC(String maLopTC);

    
    @Query("SELECT COUNT(sv) FROM SinhVienNhom sv WHERE sv.nhom.maNhom = :maNhom AND sv.ngayRoiNhom IS NULL")
    int countCurrentMembersInNhom(@Param("maNhom") int maNhom);

    List<Nhom> findByMaNhom(Integer maNhom);
    List<Nhom> findByTenNhomContainingIgnoreCase(String tenNhom);
    @Query("""
        SELECT CASE WHEN COUNT(sn) > 0 THEN true ELSE false END
        FROM SinhVienNhom sn
        JOIN sn.nhom n
        JOIN DeTai dt ON dt.nhom.maNhom = n.maNhom
        JOIN LoaiBaoCaoDeTai lbcd ON lbcd.deTai.maDT = dt.maDT
        WHERE n.lopTinChi.maLopTC = :maLopTC
        AND sn.sinhVien.maSV = :maSV
        AND lbcd.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao
        """)
    boolean existsBySinhVienInLopTinChiAndLoaiBaoCao(
            @Param("maLopTC") String maLopTC,
            @Param("maSV") String maSV,
            @Param("maLoaiBaoCao") int maLoaiBaoCao
    );

    @Query("SELECT n FROM Nhom n WHERE n.lopTinChi.maLopTC = :maLopTC AND NOT EXISTS (SELECT dt FROM DeTai dt WHERE dt.nhom.maNhom = n.maNhom AND EXISTS (SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.maDT = dt.maDT AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao))")
    List<Nhom> findNhomChuaCoDeTaiByLoaiBaoCao(@Param("maLopTC") String maLopTC, @Param("maLoaiBaoCao") int maLoaiBaoCao);

    @Query("SELECT COUNT(n) FROM Nhom n WHERE n.lopTinChi.maLopTC = :maLopTC AND NOT EXISTS (SELECT dt FROM DeTai dt WHERE dt.nhom.maNhom = n.maNhom)")
    long countNhomChuaCoDeTai(@Param("maLopTC") String maLopTC);
    @Query("SELECT n FROM Nhom n WHERE n.lopTinChi.maLopTC = :maLopTC " +
       "AND NOT EXISTS (SELECT dt FROM DeTai dt " +
       "JOIN LoaiBaoCaoDeTai lbc ON lbc.deTai.maDT = dt.maDT " +
       "WHERE dt.nhom.maNhom = n.maNhom " +
       "AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao)")
    List<Nhom> findByLopTinChi_MaLopTCAndChuaCoDeTai(
        @Param("maLopTC") String maLopTC,
        @Param("maLoaiBaoCao") int maLoaiBaoCao
);
}
