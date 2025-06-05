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

    public Page<DeTai> findDeTaisWithFilter(String keyword, String maKhoa, String maGV, String maLopTC, String maMon, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // Debug logging
        System.out.println("=== SERVICE DEBUG ===");
        System.out.println("Original keyword: " + keyword);
        System.out.println("maKhoa: " + maKhoa);
        System.out.println("maGV: " + maGV);
        System.out.println("maLopTC: " + maLopTC);
        System.out.println("maMon: " + maMon);
        
        // Clean parameters - chuyển empty string thành null
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword : null;
        String cleanMaKhoa = (maKhoa != null && !maKhoa.trim().isEmpty()) ? maKhoa : null;
        String cleanMaGV = (maGV != null && !maGV.trim().isEmpty()) ? maGV : null;
        String cleanMaLopTC = (maLopTC != null && !maLopTC.trim().isEmpty()) ? maLopTC : null;
        String cleanMaMon = (maMon != null && !maMon.trim().isEmpty()) ? maMon : null;
        
        System.out.println("Clean keyword: " + cleanKeyword);
        System.out.println("Clean maKhoa: " + cleanMaKhoa);
        System.out.println("Clean maGV: " + cleanMaGV);
        System.out.println("Clean maLopTC: " + cleanMaLopTC);
        System.out.println("Clean maMon: " + cleanMaMon);
        System.out.println("===================");
        
        return deTaiRepository.findByAdvancedFilters(cleanMaKhoa, cleanMaGV, cleanMaLopTC, cleanMaMon,
            cleanKeyword, pageable);
    }

    public Page<DeTai> findAllDeTai(int page, int size, String keyword, String filterKhoa, String filterGiangVien) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return deTaiRepository.findByFilters(filterKhoa, filterGiangVien, 
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
        
        // Kiểm tra mô tả
        if (deTai.getMoTa() == null || deTai.getMoTa().trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả không được để trống");
        }
        
        // Kiểm tra ngày bắt đầu
        if (deTai.getNgayBatDau() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }
        
        // Kiểm tra Lớp tín chỉ
        if (deTai.getLopTinChi() != null && deTai.getLopTinChi().getMaLopTC() != null && !deTai.getLopTinChi().getMaLopTC().trim().isEmpty()) {
            LopTinChi lopTinChi = lopTinChiRepository.findById(deTai.getLopTinChi().getMaLopTC())
                .orElseThrow(() -> new IllegalArgumentException("Lớp tín chỉ không tồn tại"));
            deTai.setLopTinChi(lopTinChi);
        } else {
            throw new IllegalArgumentException("Mã lớp tín chỉ không được để trống");
        }
        
        return deTaiRepository.save(deTai);
    }

    public void deleteById(int maDT) {
        deTaiRepository.deleteById(maDT);
    }
}