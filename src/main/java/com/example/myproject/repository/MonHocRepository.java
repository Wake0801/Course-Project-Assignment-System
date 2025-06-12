package com.example.myproject.repository;

import com.example.myproject.entity.MonHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MonHocRepository extends JpaRepository<MonHoc, String> {
    @Query("SELECT m FROM MonHoc m WHERE LOWER(m.maMon) LIKE %:keyword% OR LOWER(m.tenMon) LIKE %:keyword%")
    Page<MonHoc> search(String keyword, Pageable pageable);
}
