package com.example.myproject.repository;

import com.example.myproject.entity.Nhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhomRepository extends JpaRepository<Nhom, Integer> {
    List<Nhom> findByMaNhom(Integer maNhom);
    List<Nhom> findByTenNhomContainingIgnoreCase(String tenNhom);
    List<Nhom> findByMaLopTC(String maLopTC);
}
