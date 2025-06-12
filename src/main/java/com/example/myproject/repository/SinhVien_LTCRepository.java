package com.example.myproject.repository;

import com.example.myproject.entity.SinhVien_LTC;
import com.example.myproject.entity.SinhVien_LTCPK;
import com.example.myproject.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinhVien_LTCRepository extends JpaRepository<SinhVien_LTC, SinhVien_LTCPK> {
    // Lấy danh sách SinhVien_LTC theo mã lớp tín chỉ
    @Query("SELECT svltc FROM SinhVien_LTC svltc WHERE svltc.lopTinChi.maLopTC = :maLopTC")
    List<SinhVien_LTC> findByMaLopTC(@Param("maLopTC") String maLopTC);

    // Lấy danh sách sinh viên theo mã lớp tín chỉ
    @Query("SELECT svltc.sinhVien FROM SinhVien_LTC svltc WHERE svltc.lopTinChi.maLopTC = :maLopTC")
    List<SinhVien> findSinhViensByMaLopTC(@Param("maLopTC") String maLopTC);

    boolean existsById(SinhVien_LTCPK id);

    void deleteById(SinhVien_LTCPK id);
}
