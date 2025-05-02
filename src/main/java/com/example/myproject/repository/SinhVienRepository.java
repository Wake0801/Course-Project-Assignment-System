package com.example.myproject.repository;

import com.example.myproject.entity.SinhVien;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, String> {
    
    @Query("SELECT s FROM SinhVien s WHERE " +
           "LOWER(s.maSV) LIKE %:keyword% OR " +
           "LOWER(s.ho) LIKE %:keyword% OR " +
           "LOWER(s.ten) LIKE %:keyword%")
    Page<SinhVien> search(@Param("keyword") String keyword, Pageable pageable);
}
