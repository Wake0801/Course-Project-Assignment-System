package com.example.myproject.repository;

import com.example.myproject.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    // Tìm kiếm theo username (không phân biệt hoa thường)
    Page<TaiKhoan> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    // Hoặc tìm kiếm tổng quát hơn
    @Query("SELECT t FROM TaiKhoan t LEFT JOIN t.quyen q " +
           "WHERE LOWER(t.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.maTK) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.loaiTK) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(q.tenQuyen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<TaiKhoan> searchAccounts(@Param("keyword") String keyword, Pageable pageable);
} 