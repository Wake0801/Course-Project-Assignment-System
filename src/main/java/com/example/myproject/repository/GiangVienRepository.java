package com.example.myproject.repository;

import com.example.myproject.entity.GiangVien; // Đảm bảo import đúng entity GiangVien
import com.example.myproject.entity.TaiKhoan;

import java.util.List;
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

    // tìm kiếm GiangVien
    @Query("SELECT g FROM GiangVien g WHERE " +
           "UPPER(TRIM(g.maGV)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.ho)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.ten)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.soDT)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.email)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.hocVi)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.khoa.tenKhoa)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(g.ho, ' ', g.ten))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(g.ten, ' ', g.ho))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%'))")
    Page<GiangVien> search(@Param("keyword") String keyword, Pageable pageable);
    
    // Tìm kiếm với filter theo khoa
    @Query("SELECT g FROM GiangVien g WHERE g.khoa.maKhoa = :maKhoa AND (" +
           "UPPER(TRIM(g.maGV)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.ho)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.ten)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.soDT)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.email)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(g.hocVi)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(g.ho, ' ', g.ten))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(g.ten, ' ', g.ho))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')))")
    Page<GiangVien> searchByKhoa(@Param("keyword") String keyword, @Param("maKhoa") String maKhoa, Pageable pageable);
    
    // Filter chỉ theo khoa
    Page<GiangVien> findByKhoa_MaKhoa(String maKhoa, Pageable pageable);
    
    // Lấy danh sách giảng viên theo khoa (không phân trang - cho dropdown)
    List<GiangVien> findByKhoa_MaKhoa(String maKhoa);
    
    Optional<GiangVien> findByTaiKhoan_MaTK(Integer maTK);
    
    boolean existsByTaiKhoan_MaTK(Integer maTK);
    
    // Tìm giảng viên chưa có tài khoản
    List<GiangVien> findByTaiKhoanIsNull();
}