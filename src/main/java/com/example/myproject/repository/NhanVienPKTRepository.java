package com.example.myproject.repository;

import com.example.myproject.entity.NhanVienPKT;
import com.example.myproject.entity.TaiKhoan;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienPKTRepository extends JpaRepository<NhanVienPKT, String> {

    @Query("SELECT nv FROM NhanVienPKT nv WHERE " +
           "UPPER(TRIM(nv.maNV)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(nv.ho)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(nv.ten)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(nv.soDT)) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(nv.ho, ' ', nv.ten))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%')) OR " +
           "UPPER(TRIM(CONCAT(nv.ten, ' ', nv.ho))) LIKE UPPER(CONCAT('%', TRIM(:keyword), '%'))")
    Page<NhanVienPKT> search(@Param("keyword") String keyword, Pageable pageable);
    
    Optional<NhanVienPKT> findByTaiKhoan_MaTK(String maTK);
    
    boolean existsByTaiKhoan_MaTK(String maTK);
}