package com.example.myproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.myproject.compositeKey.SinhVienNhomId;
import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.entity.SinhVienNhom;

@Repository
public interface SinhVienNhomRepository extends JpaRepository<SinhVienNhom, SinhVienNhomId> {
    @Query("SELECT sn.sinhVien FROM SinhVienNhom sn WHERE sn.sinhVienNhomId.maNhom = :maNhom")
    List<SinhVien> findByNhom(@Param("maNhom") int maNhom);

    @Query("SELECT sn.nhom FROM SinhVienNhom sn WHERE sn.sinhVienNhomId.maSV = :maSV")
    List<Nhom> findBySinhVien(@Param("maSV") String maSV);

    @Query("SELECT COUNT(sn) > 0 FROM SinhVienNhom sn WHERE sn.sinhVienNhomId.maSV = :maSV AND sn.sinhVienNhomId.maNhom = :maNhom AND sn.ngayRoiNhom IS NULL")
    boolean existsByMaSVAndMaNhom(@Param("maSV") String maSV, @Param("maNhom") int maNhom);
        boolean existsBySinhVien_MaSVAndNhom_LopTinChi_MaLopTCAndNgayRoiNhomIsNull(String maSV, String maLopTC);
    
    @Query("SELECT sn FROM SinhVienNhom sn WHERE sn.sinhVien.maSV = :maSV AND sn.nhom.lopTinChi.maLopTC = :maLopTC AND sn.ngayRoiNhom IS NULL")
    Optional<SinhVienNhom> findCurrentNhomOfSinhVienInLopTC(@Param("maSV") String maSV, @Param("maLopTC") String maLopTC);
    @Query("SELECT sn FROM SinhVienNhom sn WHERE sn.nhom.maNhom = :maNhom")
    List<SinhVienNhom> findByNhom_MaNhom(@Param("maNhom") int maNhom);
    
    boolean existsByNhom_MaNhomAndSinhVien_MaSV(Integer maNhom, String maSV);
    void deleteByNhom_MaNhomAndSinhVien_MaSV(Integer maNhom, String maSV);

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
    boolean existsSinhVienTrungBaoCao(
            @Param("maLopTC") String maLopTC,
            @Param("maSV") String maSV,
            @Param("maLoaiBaoCao") Integer maLoaiBaoCao
    );
}
