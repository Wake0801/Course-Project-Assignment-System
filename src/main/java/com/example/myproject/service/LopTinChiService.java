package com.example.myproject.service;

import com.example.myproject.entity.LopTinChi;
import com.example.myproject.repository.LopTinChiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LopTinChiService {
    
    @Autowired
    private LopTinChiRepository lopTinChiRepository;
    
    public List<LopTinChi> getAllLopTinChi() {
        return lopTinChiRepository.findAll();
    }
    
    public LopTinChi findById(String maLopTC) {
        return lopTinChiRepository.findById(maLopTC).orElse(null);
    }
    
    // Lấy lớp tín chỉ theo khoa (thông qua giảng viên)
    public List<LopTinChi> getLopTinChiByKhoa(String maKhoa) {
        if (maKhoa == null || maKhoa.trim().isEmpty()) {
            return getAllLopTinChi();
        }
        return lopTinChiRepository.findByGiangVien_Khoa_MaKhoa(maKhoa);
    }
    
    // Lấy lớp tín chỉ theo giảng viên
    public List<LopTinChi> getLopTinChiByGiangVien(String maGV) {
        if (maGV == null || maGV.trim().isEmpty()) {
            return getAllLopTinChi();
        }
        return lopTinChiRepository.findByGiangVien_MaGV(maGV);
    }
} 