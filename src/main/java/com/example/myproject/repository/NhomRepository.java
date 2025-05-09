package com.example.myproject.repository;

import com.example.myproject.entity.Nhom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NhomRepository extends JpaRepository<Nhom, String> {
    
    @Query("SELECT n FROM Nhom n WHERE " +
           "LOWER(n.maNhom) LIKE %:keyword% OR " +
           "LOWER(n.tenNhom) LIKE %:keyword%")
    Page<Nhom> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT n.*, " +
           "(SELECT COUNT(svn.MaSV) FROM SinhVienNhom svn " +
           "WHERE svn.MaNhom = n.MaNhom AND svn.NgayRoiNhom IS NULL) as memberCount " +
           "FROM Nhom n", nativeQuery = true)
    Page<Nhom> findAllWithMemberCount(Pageable pageable);

    @Query("SELECT COUNT(svn) FROM SinhVienNhom svn WHERE svn.nhom.maNhom = :maNhom AND svn.ngayRoiNhom IS NULL")
    long countMembersByMaNhom(@Param("maNhom") String maNhom);
}