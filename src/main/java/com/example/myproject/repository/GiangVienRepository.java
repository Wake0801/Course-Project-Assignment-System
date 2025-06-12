package com.example.myproject.repository;

import com.example.myproject.entity.GiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiangVienRepository extends JpaRepository<GiangVien, String> {
    // Có thể thêm phương thức custom nếu cần
}
