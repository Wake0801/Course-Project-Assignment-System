package com.example.myproject.service;

import com.example.myproject.entity.NhanVienPKT;
import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.repository.NhanVienPKTRepository;
import com.example.myproject.repository.LoginRepository;
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
public class NhanVienPKTService {

    @Autowired
    private NhanVienPKTRepository nhanVienPKTRepository;

    @Autowired
    private LoginRepository taiKhoanRepository;

    public Page<NhanVienPKT> findNhanViens(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return nhanVienPKTRepository.search(keyword.toLowerCase(), pageable);
        }
        return nhanVienPKTRepository.findAll(pageable);
    }

    public NhanVienPKT save(NhanVienPKT nhanVien) {
        // Kiểm tra ngày sinh
        if (nhanVien.getNgaySinh() == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống");
        }
        
        // Kiểm tra tài khoản
        if (nhanVien.getTaiKhoan() != null && nhanVien.getTaiKhoan().getMaTK() != null && !nhanVien.getTaiKhoan().getMaTK().trim().isEmpty()) {
            TaiKhoan tk = taiKhoanRepository.findById(nhanVien.getTaiKhoan().getMaTK())
                                        .orElseThrow(() -> new IllegalArgumentException("Mã tài khoản không tồn tại"));
            nhanVien.setTaiKhoan(tk);
        } else {
             throw new IllegalArgumentException("Mã tài khoản không được để trống");
        }
        return nhanVienPKTRepository.save(nhanVien);
    }

    public void deleteById(String maNV) {
        nhanVienPKTRepository.deleteById(maNV);
    }

    public Optional<NhanVienPKT> findById(String maNV) {
        return nhanVienPKTRepository.findById(maNV);
    }
}