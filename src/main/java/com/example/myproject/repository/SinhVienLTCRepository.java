package com.example.myproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.myproject.compositeKey.SinhVienLTCId;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.SinhVien_LTC;

public interface SinhVienLTCRepository extends JpaRepository<SinhVien_LTC, SinhVienLTCId> {
    @Query("SELECT svltc.lopTinChi FROM SinhVien_LTC svltc WHERE svltc.sinhVienLTCId.maSV = :maSV")
    List<LopTinChi> findLopTinChiByMaSV(@Param("maSV") String maSV);

}
