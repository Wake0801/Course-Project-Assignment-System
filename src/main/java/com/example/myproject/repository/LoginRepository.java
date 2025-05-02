package com.example.myproject.repository;

import com.example.myproject.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<TaiKhoan, String> {
    // Tìm TaiKhoan theo username
    Optional<TaiKhoan> findByUsername(String username);
}