package com.example.myproject.service;

import com.example.myproject.entity.Lop;
import com.example.myproject.repository.LopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LopService {
    
    @Autowired
    private LopRepository lopRepository;
    
    public List<Lop> getAllLop() {
        return lopRepository.findAll();
    }
    
    public Lop findById(String maLop) {
        return lopRepository.findById(maLop).orElse(null);
    }
} 