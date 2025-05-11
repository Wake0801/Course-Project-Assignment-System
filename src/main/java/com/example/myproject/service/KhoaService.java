package com.example.myproject.service;

import com.example.myproject.entity.Khoa;
import com.example.myproject.repository.KhoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KhoaService {

    @Autowired
    private KhoaRepository khoaRepository;

    public List<Khoa> getAllKhoa() {
        return khoaRepository.findAll();
    }

    public Khoa save(Khoa khoa) {
        return khoaRepository.save(khoa);
    }

    public void deleteById(String maKhoa) {
        khoaRepository.deleteById(maKhoa);
    }

    public Khoa findById(String maKhoa) {
        return khoaRepository.findById(maKhoa)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoa với mã: " + maKhoa));
    }
} 