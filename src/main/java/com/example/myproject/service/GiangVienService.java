package com.example.myproject.service;

import com.example.myproject.entity.GiangVien; //
import com.example.myproject.entity.Khoa;
import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.repository.GiangVienRepository;
import com.example.myproject.repository.KhoaRepository; // Import KhoaRepository
import com.example.myproject.repository.LoginRepository; // Import LoginRepository (để quản lý TaiKhoan)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.time.LocalDate;

@Service
@Transactional
public class GiangVienService {

    @Autowired
    private GiangVienRepository giangVienRepository;

    @Autowired
    private KhoaRepository khoaRepository; // Inject KhoaRepository

    @Autowired
    private LoginRepository taiKhoanRepository; // Inject LoginRepository

    // Tìm kiếm và phân trang giảng viên
    public Page<GiangVien> findGiangViens(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return giangVienRepository.search(keyword.trim(), pageable);
        }
        return giangVienRepository.findAll(pageable);
    }

    // Tìm kiếm với filter theo khoa
    public Page<GiangVien> findGiangViensWithFilter(String keyword, String maKhoa, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // Nếu có filter theo khoa
        if (maKhoa != null && !maKhoa.trim().isEmpty()) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Có cả tìm kiếm và filter khoa
                return giangVienRepository.searchByKhoa(keyword.trim(), maKhoa, pageable);
            } else {
                // Chỉ filter theo khoa
                return giangVienRepository.findByKhoa_MaKhoa(maKhoa, pageable);
            }
        } else {
            // Không có filter khoa, chỉ tìm kiếm
            if (keyword != null && !keyword.trim().isEmpty()) {
                return giangVienRepository.search(keyword.trim(), pageable);
            }
            return giangVienRepository.findAll(pageable);
        }
    }

    // Lưu thông tin giảng viên (Thêm mới hoặc cập nhật)
    public GiangVien save(GiangVien giangVien) {
        // Kiểm tra dữ liệu ngày sinh
        if (giangVien.getNgaySinh() == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống");
        }
        
        // Kiểm tra số điện thoại
        if (giangVien.getSoDT() != null && !giangVien.getSoDT().trim().isEmpty()) {
            String soDT = giangVien.getSoDT().trim();
            if (!soDT.matches("^0\\d{9}$")) {
                throw new IllegalArgumentException("Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số");
            }
        }
        
        // Xử lý email - cho phép null hoặc rỗng
        if (giangVien.getEmail() != null && giangVien.getEmail().trim().isEmpty()) {
            giangVien.setEmail(null);
        }
        
        // Xử lý quan hệ Khoa
        if (giangVien.getKhoa() != null && giangVien.getKhoa().getMaKhoa() != null && !giangVien.getKhoa().getMaKhoa().trim().isEmpty()) {
            Khoa khoa = khoaRepository.findById(giangVien.getKhoa().getMaKhoa())
                                   .orElseThrow(() -> new IllegalArgumentException("Mã khoa không tồn tại"));
            giangVien.setKhoa(khoa);
        } else {
             throw new IllegalArgumentException("Mã khoa không được để trống");
        }

        // Xử lý quan hệ tài khoản - Cải thiện logic để tránh UNIQUE constraint với NULL
        if (giangVien.getTaiKhoan() != null) {
            Integer maTK = giangVien.getTaiKhoan().getMaTK();
            
            // Nếu MaTK là null, empty, hoặc 0 thì set toàn bộ taiKhoan thành null
            if (maTK == null || maTK == 0) {
                giangVien.setTaiKhoan(null);
            } else {
                // Validate MaTK tồn tại
                TaiKhoan tk = taiKhoanRepository.findById(maTK)
                                            .orElseThrow(() -> new IllegalArgumentException("Mã tài khoản không tồn tại: " + maTK));
                
                // Kiểm tra MaTK đã được sử dụng bởi giảng viên khác chưa
                if (!giangVien.getMaGV().equals(getCurrentGiangVienByMaTK(maTK))) {
                    throw new IllegalArgumentException("Tài khoản này đã được gán cho giảng viên khác");
                }
                
                giangVien.setTaiKhoan(tk);
            }
        } else {
            // TaiKhoan object null -> set thành null 
            giangVien.setTaiKhoan(null);
        }

        return giangVienRepository.save(giangVien);
    }
    
    // Helper method để kiểm tra MaTK đã được sử dụng chưa
    private String getCurrentGiangVienByMaTK(Integer maTK) {
        Optional<GiangVien> existingGiangVien = giangVienRepository.findByTaiKhoan_MaTK(maTK);
        return existingGiangVien.map(GiangVien::getMaGV).orElse(null);
    }

    // Xóa giảng viên theo ID
    public void deleteById(String maGV) {
        giangVienRepository.deleteById(maGV);
    }

    // Tìm giảng viên theo ID
    public Optional<GiangVien> findById(String maGV) {
        return giangVienRepository.findById(maGV);
    }
    
    // Lấy tất cả giảng viên
    public java.util.List<GiangVien> getAllGiangVien() {
        return giangVienRepository.findAll();
    }
    
    // Lấy giảng viên theo khoa (cho dynamic filtering)
    public java.util.List<GiangVien> getGiangVienByKhoa(String maKhoa) {
        if (maKhoa == null || maKhoa.trim().isEmpty()) {
            return getAllGiangVien();
        }
        return giangVienRepository.findByKhoa_MaKhoa(maKhoa);
    }
}