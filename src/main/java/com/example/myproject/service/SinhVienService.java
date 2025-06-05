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
            return sinhVienRepository.search(keyword.trim(), pageable);
        }

        return sinhVienRepository.findAll(pageable);
    }

    // Tìm kiếm với filter theo khoa và lớp
    public Page<SinhVien> findSinhViensWithFilter(String keyword, String maKhoa, String maLop, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // Logic filter phức tạp - ưu tiên filter theo lớp trước
        if (maLop != null && !maLop.trim().isEmpty()) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Có cả tìm kiếm và filter lớp
                return sinhVienRepository.searchByLop(keyword.trim(), maLop, pageable);
            } else {
                // Chỉ filter theo lớp
                return sinhVienRepository.findByLop_MaLop(maLop, pageable);
            }
        } else if (maKhoa != null && !maKhoa.trim().isEmpty()) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Có cả tìm kiếm và filter khoa
                return sinhVienRepository.searchByKhoa(keyword.trim(), maKhoa, pageable);
            } else {
                // Chỉ filter theo khoa
                return sinhVienRepository.findByLop_Khoa_MaKhoa(maKhoa, pageable);
            }
        } else {
            // Không có filter khoa/lớp, chỉ tìm kiếm
            if (keyword != null && !keyword.trim().isEmpty()) {
                return sinhVienRepository.search(keyword.trim(), pageable);
            }
            return sinhVienRepository.findAll(pageable);
        }
    }

    public SinhVien save(SinhVien sinhVien) {
        // Kiểm tra số điện thoại
        if (sinhVien.getSoDT() != null && !sinhVien.getSoDT().trim().isEmpty()) {
            String soDT = sinhVien.getSoDT().trim();
            if (!soDT.matches("^0\\d{9}$")) {
                throw new IllegalArgumentException("Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số");
            }
        }
        
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

