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
        
        // Xử lý quan hệ Khoa
        if (giangVien.getKhoa() != null && giangVien.getKhoa().getMaKhoa() != null && !giangVien.getKhoa().getMaKhoa().trim().isEmpty()) {
            Khoa khoa = khoaRepository.findById(giangVien.getKhoa().getMaKhoa())
                                   .orElseThrow(() -> new IllegalArgumentException("Mã khoa không tồn tại"));
            giangVien.setKhoa(khoa);
        } else {
             throw new IllegalArgumentException("Mã khoa không được để trống");
        }

        // Xử lý quan hệ Tài khoản
        if (giangVien.getTaiKhoan() != null && giangVien.getTaiKhoan().getMaTK() != null && !giangVien.getTaiKhoan().getMaTK().trim().isEmpty()) {
            TaiKhoan tk = taiKhoanRepository.findById(giangVien.getTaiKhoan().getMaTK())
                                        .orElseThrow(() -> new IllegalArgumentException("Mã tài khoản không tồn tại"));
            giangVien.setTaiKhoan(tk);
        } else {
            throw new IllegalArgumentException("Mã tài khoản không được để trống");
        }

        return giangVienRepository.save(giangVien);
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