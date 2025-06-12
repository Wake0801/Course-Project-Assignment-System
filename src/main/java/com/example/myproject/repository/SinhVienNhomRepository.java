package com.example.myproject.repository;

import java.util.List;

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
}
