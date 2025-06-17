package com.example.myproject.service;

import com.example.myproject.entity.NhanVienPKT;
import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.repository.NhanVienPKTRepository;
import com.example.myproject.repository.LoginRepository;
import com.example.myproject.util.StringUtils;
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
            return nhanVienPKTRepository.search(keyword.trim(), pageable);
        }
        return nhanVienPKTRepository.findAll(pageable);
    }

    public NhanVienPKT save(NhanVienPKT nhanVien) {
        // Kiểm tra ngày sinh
        if (nhanVien.getNgaySinh() == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống");
        }
        
        // Kiểm tra số điện thoại
        if (nhanVien.getSoDT() != null && !nhanVien.getSoDT().trim().isEmpty()) {
            String soDT = nhanVien.getSoDT().trim();
            if (!soDT.matches("^0\\d{9}$")) {
                throw new IllegalArgumentException("Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số");
            }
        }
        
        // Xử lý email - cho phép null hoặc rỗng
        if (nhanVien.getEmail() != null && nhanVien.getEmail().trim().isEmpty()) {
            nhanVien.setEmail(null);
        }
        
        // Xử lý quan hệ tài khoản - Cải thiện logic để tránh UNIQUE constraint với NULL
        if (nhanVien.getTaiKhoan() != null) {
            Integer maTK = nhanVien.getTaiKhoan().getMaTK();
            
            // Nếu MaTK là null, empty, hoặc 0 thì set toàn bộ taiKhoan thành null
            if (maTK == null || maTK == 0) {
                nhanVien.setTaiKhoan(null);
            } else {
                // Validate MaTK tồn tại
                TaiKhoan tk = taiKhoanRepository.findById(maTK)
                                            .orElseThrow(() -> new IllegalArgumentException("Mã tài khoản không tồn tại: " + maTK));
                
                // Kiểm tra MaTK đã được sử dụng bởi nhân viên khác chưa
                if (!nhanVien.getMaNV().equals(getCurrentNhanVienByMaTK(maTK))) {
                    throw new IllegalArgumentException("Tài khoản này đã được gán cho nhân viên khác");
                }
                
                nhanVien.setTaiKhoan(tk);
            }
        } else {
            // TaiKhoan object null -> set thành null 
            nhanVien.setTaiKhoan(null);
        }
        return nhanVienPKTRepository.save(nhanVien);
    }
    
    // Helper method để kiểm tra MaTK đã được sử dụng chưa
    private String getCurrentNhanVienByMaTK(Integer maTK) {
        Optional<NhanVienPKT> existingNhanVien = nhanVienPKTRepository.findByTaiKhoan_MaTK(maTK);
        return existingNhanVien.map(NhanVienPKT::getMaNV).orElse(null);
    }

    public void deleteById(String maNV) {
        nhanVienPKTRepository.deleteById(maNV);
    }

    public Optional<NhanVienPKT> findById(String maNV) {
        return nhanVienPKTRepository.findById(maNV);
    }
}