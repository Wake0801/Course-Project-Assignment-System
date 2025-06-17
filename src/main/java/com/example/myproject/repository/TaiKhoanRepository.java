package com.example.myproject.repository;

import com.example.myproject.entity.TaiKhoan;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    // Tìm kiếm theo username (không phân biệt hoa thường)
    Page<TaiKhoan> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Optional<TaiKhoan> findByUsername(String username);
    // Hoặc tìm kiếm tổng quát hơn
    @Query("SELECT t FROM TaiKhoan t LEFT JOIN t.quyen q " +
           "WHERE LOWER(t.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR CAST(t.maTK AS STRING) LIKE CONCAT('%', :keyword, '%') " +
           "OR LOWER(t.loaiTK) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(q.tenQuyen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<TaiKhoan> searchAccounts(@Param("keyword") String keyword, Pageable pageable);
    
    // Tìm tài khoản có mã lớn nhất
    Optional<TaiKhoan> findTopByOrderByMaTKDesc();
} 