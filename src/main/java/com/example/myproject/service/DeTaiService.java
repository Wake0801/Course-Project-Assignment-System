package com.example.myproject.service;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.Khoa;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.repository.DeTaiRepository;
import com.example.myproject.repository.GiangVienRepository;
import com.example.myproject.repository.KhoaRepository;
import com.example.myproject.repository.LopTinChiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeTaiService {

    @Autowired
    private DeTaiRepository deTaiRepository;

    @Autowired
    private GiangVienRepository giangVienRepository;

    @Autowired
    private KhoaRepository khoaRepository;
    
    @Autowired
    private LopTinChiRepository lopTinChiRepository;

    public Page<DeTai> findAllDeTai(int page, int size, String keyword, String filterKhoa, String filterGiangVien) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return deTaiRepository.findByFilters(filterKhoa, filterGiangVien, 
            keyword != null ? keyword.toLowerCase() : "", 
            pageable);
    }

    public Page<DeTai> findByAdvancedFilters(int page, int size, String keyword, String maKhoa, String maGV, String maLopTC, String maMon) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return deTaiRepository.findByAdvancedFilters(maKhoa, maGV, maLopTC, maMon, 
            keyword != null ? keyword.toLowerCase() : "", 
            pageable);
    }

    public List<DeTai> getAllDeTai() {
        return deTaiRepository.findAll();
    }

    public List<Khoa> getAllKhoa() {
        return khoaRepository.findAll();
    }

    public List<GiangVien> getAllGiangVien() {
        return giangVienRepository.findAll();
    }
    
    public List<LopTinChi> getAllLopTinChi() {
        return lopTinChiRepository.findAll();
    }

    public Optional<DeTai> findById(int maDT) {
        return deTaiRepository.findById(maDT);
    }

    public DeTai save(DeTai deTai) {
        // Kiểm tra tên đề tài
        if (deTai.getTenDT() == null || deTai.getTenDT().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đề tài không được để trống");
        }
        
        // Kiểm tra độ dài tên đề tài
        if (deTai.getTenDT().length() > 50) {
            throw new IllegalArgumentException("Tên đề tài không được vượt quá 50 ký tự");
        }
        
        // Kiểm tra mô tả
        if (deTai.getMoTa() == null || deTai.getMoTa().trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả không được để trống");
        }
        
        // Kiểm tra ngày bắt đầu
        if (deTai.getNgayBatDau() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }
        
        // Kiểm tra Lớp tín chỉ
        if (deTai.getLopTinChi() == null || deTai.getLopTinChi().getMaLopTC() == null || deTai.getLopTinChi().getMaLopTC().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã lớp tín chỉ không được để trống");
        } else {
            LopTinChi lopTinChi = lopTinChiRepository.findById(deTai.getLopTinChi().getMaLopTC())
                .orElseThrow(() -> new IllegalArgumentException("Lớp tín chỉ không tồn tại"));
            deTai.setLopTinChi(lopTinChi);
        }
        
        // Kiểm tra Nhóm (REQUIRED theo database schema)
        if (deTai.getNhom() == null || deTai.getNhom().getMaNhom() <= 0) {
            throw new IllegalArgumentException("Nhóm không được để trống");
        }
        
        return deTaiRepository.save(deTai);
    }

    public void deleteById(int maDT) {
        deTaiRepository.deleteById(maDT);
    }
}