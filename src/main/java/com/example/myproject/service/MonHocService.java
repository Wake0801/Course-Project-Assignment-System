package com.example.myproject.service;

import com.example.myproject.entity.MonHoc;
import com.example.myproject.repository.MonHocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MonHocService {

    @Autowired
    private MonHocRepository monHocRepository;

    public Page<MonHoc> findMonHocs(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return monHocRepository.search(keyword.toLowerCase(), pageable);
        }
        return monHocRepository.findAll(pageable);
    }

    public MonHoc save(MonHoc monHoc) {
        return monHocRepository.save(monHoc);
    }

    public void deleteById(String maMon) {
        monHocRepository.deleteById(maMon);
    }

    public Optional<MonHoc> findById(String maMon) {
        return monHocRepository.findById(maMon);
    }
}
