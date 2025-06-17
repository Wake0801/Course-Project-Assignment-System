package com.example.myproject.repository;

import com.example.myproject.entity.SinhVien;
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
public interface SinhVienRepository extends JpaRepository<SinhVien, String> {
    
    // Tìm kiếm sinh viên
    @Query("SELECT s FROM SinhVien s WHERE " +
           "UPPER(TRIM(s.maSV)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.ho)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.ten)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.soDT)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.email)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(s.ho, ' ', s.ten))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(s.ten, ' ', s.ho))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%'))")
    Page<SinhVien> search(@Param("keyword") String keyword, Pageable pageable);
    
    // Tìm kiếm với filter theo lớp
    @Query("SELECT s FROM SinhVien s WHERE s.lop.maLop = :maLop AND (" +
           "UPPER(TRIM(s.maSV)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.ho)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.ten)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.soDT)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.email)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(s.ho, ' ', s.ten))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(s.ten, ' ', s.ho))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')))")
    Page<SinhVien> searchByLop(@Param("keyword") String keyword, @Param("maLop") String maLop, Pageable pageable);
    
    // Tìm kiếm với filter theo khoa (qua quan hệ lớp)
    @Query("SELECT s FROM SinhVien s WHERE s.lop.khoa.maKhoa = :maKhoa AND (" +
           "UPPER(TRIM(s.maSV)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.ho)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.ten)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.soDT)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(s.email)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(s.ho, ' ', s.ten))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(s.ten, ' ', s.ho))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')))")
    Page<SinhVien> searchByKhoa(@Param("keyword") String keyword, @Param("maKhoa") String maKhoa, Pageable pageable);
    
    // Filter chỉ theo lớp
    Page<SinhVien> findByLop_MaLop(String maLop, Pageable pageable);
    
    // Filter chỉ theo khoa (qua quan hệ lớp)
    Page<SinhVien> findByLop_Khoa_MaKhoa(String maKhoa, Pageable pageable);
    
    // Tìm kiếm tài khoản sinh viên
    Optional<SinhVien> findByTaiKhoan_MaTK(Integer maTK);
    
    // Kiểm tra tồn tại tài khoản sinh viên
    boolean existsByTaiKhoan_MaTK(Integer maTK);
    
    // Tìm sinh viên chưa có tài khoản
    List<SinhVien> findByTaiKhoanIsNull();

}
