package com.example.myproject.repository;

import com.example.myproject.entity.GiangVien; // Đảm bảo import đúng entity GiangVien
import com.example.myproject.entity.TaiKhoan;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
// Sử dụng String làm kiểu dữ liệu cho ID (MaGV) giống như entity GiangVien
public interface GiangVienRepository extends JpaRepository<GiangVien, String> { 

    // Câu query tìm kiếm tương tự SinhVienRepository nhưng tìm theo các trường của GiangVien
    @Query("SELECT g FROM GiangVien g WHERE " +
           "LOWER(g.maGV) LIKE %:keyword% OR " +
           "LOWER(g.ho) LIKE %:keyword% OR " +
           "LOWER(g.ten) LIKE %:keyword% OR " + 
           "LOWER(g.hocVi) LIKE %:keyword% OR " +
           "LOWER(g.khoa.tenKhoa) LIKE %:keyword%") // Thêm tìm kiếm theo tên Khoa nếu cần
    Page<GiangVien> search(@Param("keyword") String keyword, Pageable pageable);
    
    Optional<GiangVien> findByTaiKhoan_MaTK(String maTK);
    
    boolean existsByTaiKhoan_MaTK(String maTK);
}