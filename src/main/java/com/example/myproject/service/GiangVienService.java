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

    // Tìm kiếm và phân trang giảng viên, tương tự SinhVienService
    public Page<GiangVien> findGiangViens(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return giangVienRepository.search(keyword.toLowerCase(), pageable);
        }
        return giangVienRepository.findAll(pageable);
    }

    // Lưu thông tin giảng viên (Thêm mới hoặc cập nhật)
    public GiangVien save(GiangVien giangVien) {
        // Kiểm tra dữ liệu ngày sinh
        if (giangVien.getNgaySinh() == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống");
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
}