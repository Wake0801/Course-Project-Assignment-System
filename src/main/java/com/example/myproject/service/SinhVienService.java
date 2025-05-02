package com.example.myproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.myproject.entity.Lop;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.repository.LoginRepository;
import com.example.myproject.repository.LopRepository;
import com.example.myproject.repository.SinhVienRepository;

import jakarta.transaction.Transactional;

import java.util.Optional;


@Service
@Transactional
public class SinhVienService{

    @Autowired
    private SinhVienRepository sinhVienRepository;
    @Autowired
    private LopRepository lopRepository;
    @Autowired
    private LoginRepository taiKhoanRepository;
    public Page<SinhVien> findSinhViens(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return sinhVienRepository.search(keyword.toLowerCase(), pageable);
        }

        return sinhVienRepository.findAll(pageable);
    }

    public SinhVien save(SinhVien sinhVien) {
        // Xử lý quan hệ lớp
        if (sinhVien.getLop() != null && sinhVien.getLop().getMaLop() != null) {
            Lop lop = lopRepository.findById(sinhVien.getLop().getMaLop())
                                .orElseThrow(() -> new IllegalArgumentException("Mã lớp không tồn tại"));
            sinhVien.setLop(lop);
        }
        
        // Xử lý quan hệ tài khoản
        if (sinhVien.getTaiKhoan() != null && sinhVien.getTaiKhoan().getMaTK() != null) {
            TaiKhoan tk = taiKhoanRepository.findById(sinhVien.getTaiKhoan().getMaTK())
                                        .orElseThrow(() -> new IllegalArgumentException("Mã tài khoản không tồn tại"));
            sinhVien.setTaiKhoan(tk);
        }
        
        return sinhVienRepository.save(sinhVien);
    }

    public void deleteById(String maSV) {
        sinhVienRepository.deleteById(maSV);
    }

    public Optional<SinhVien> findById(String maSV) {
        return sinhVienRepository.findById(maSV);
    }
}

