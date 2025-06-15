package com.example.myproject.repository;

import com.example.myproject.entity.SinhVienNhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinhVienNhomRepository extends JpaRepository<SinhVienNhom, SinhVienNhom.PK> {
    List<SinhVienNhom> findByNhom_MaNhom(Integer maNhom);
    boolean existsByNhom_MaNhomAndSinhVien_MaSV(Integer maNhom, String maSV);
    void deleteByNhom_MaNhomAndSinhVien_MaSV(Integer maNhom, String maSV);
}
