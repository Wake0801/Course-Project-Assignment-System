package com.example.myproject.service;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.Khoa;
import com.example.myproject.repository.DeTaiRepository;
import com.example.myproject.repository.GiangVienRepository;
import com.example.myproject.repository.KhoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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

    public Optional<DeTai> findById(String maDT) {
        return deTaiRepository.findById(maDT);
    }

    public DeTai save(DeTai deTai) {
        return deTaiRepository.save(deTai);
    }

    public void deleteById(String maDT) {
        deTaiRepository.deleteById(maDT);
    }
}