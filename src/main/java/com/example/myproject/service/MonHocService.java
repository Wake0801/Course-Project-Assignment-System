package com.example.myproject.service;

import com.example.myproject.entity.MonHoc;
import com.example.myproject.repository.MonHocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonHocService {
    
    @Autowired
    private MonHocRepository monHocRepository;
    
    public List<MonHoc> getAllMonHoc() {
        return monHocRepository.findAll();
    }
    
    public MonHoc findById(String maMon) {
        return monHocRepository.findById(maMon).orElse(null);
    }
    
    // Lấy môn học theo khoa (thông qua lớp tín chỉ → giảng viên)
    public List<MonHoc> getMonHocByKhoa(String maKhoa) {
        if (maKhoa == null || maKhoa.trim().isEmpty()) {
            return getAllMonHoc();
        }
        return monHocRepository.findDistinctByLopTinChis_GiangVien_Khoa_MaKhoa(maKhoa);
    }
    
    // Lấy môn học theo giảng viên (thông qua lớp tín chỉ)
    public List<MonHoc> getMonHocByGiangVien(String maGV) {
        if (maGV == null || maGV.trim().isEmpty()) {
            return getAllMonHoc();
        }
        return monHocRepository.findDistinctByLopTinChis_GiangVien_MaGV(maGV);
    }
} 