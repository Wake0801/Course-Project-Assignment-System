package com.example.myproject.repository;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.LopTinChi;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LopTinChiRepository extends JpaRepository<LopTinChi, String> {

    Optional<LopTinChi> findByMaLopTC(String maLopTC);
    
} 