package com.example.myproject.service;

import com.example.myproject.entity.LopTinChi;
import com.example.myproject.repository.LopTinChiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class LopTinChiService {

    @Autowired
    private LopTinChiRepository lopTinChiRepository;

    public Page<LopTinChi> findLopTinChis(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // page-1 vì Spring Data JPA bắt đầu từ 0
        if (keyword != null && !keyword.trim().isEmpty()) {
            return lopTinChiRepository.search(keyword.toLowerCase(), pageable);
        }
        return lopTinChiRepository.findAll(pageable);
    }

    public LopTinChi save(LopTinChi ltc) {
        return lopTinChiRepository.save(ltc);
    }

    public void deleteById(String maLopTC) {
        lopTinChiRepository.deleteById(maLopTC);
    }

    public Optional<LopTinChi> findById(String maLopTC) {
        return lopTinChiRepository.findById(maLopTC);
    }

    public Page<LopTinChi> findLopTinChisAdvanced(
            String keyword,
            String nienKhoa,
            Integer hocKi,
            String maGV,
            Boolean trangThai,
            int page,
            int size,
            String sortNgayLap // "asc" hoặc "desc"
    ) {
        Sort sort = Sort.by("ngayLap");
        if ("desc".equalsIgnoreCase(sortNgayLap)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        return lopTinChiRepository.searchAdvanced(
                keyword != null ? keyword.toLowerCase() : null,
                nienKhoa,
                hocKi,
                maGV,
                trangThai,
                pageable
        );
    }

    public List<String> findAllNienKhoa() {
        return lopTinChiRepository.findAll()
                .stream()
                .map(LopTinChi::getNienKhoa)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<LopTinChi> getAll() {
        return lopTinChiRepository.findAll();
    }
}
