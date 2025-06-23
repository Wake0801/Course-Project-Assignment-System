package com.example.myproject.service;

import com.example.myproject.entity.SinhVien;
import com.example.myproject.repository.SinhVienLTCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DSSVService {

    @Autowired
    private SinhVienLTCRepository sinhVienLTCRepository;

    public Page<SinhVien> getSinhVienByLopTinChi(String maLopTC, Pageable pageable) {
        List<SinhVien> sinhViens = sinhVienLTCRepository.findSinhViensByMaLopTC(maLopTC);
        
        // Phân trang thủ công (vì JPA không hỗ trợ phân trang với custom query trả về List)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sinhViens.size());
        
        return new PageImpl<>(
            sinhViens.subList(start, end), 
            pageable, 
            sinhViens.size()
        );
    }
}