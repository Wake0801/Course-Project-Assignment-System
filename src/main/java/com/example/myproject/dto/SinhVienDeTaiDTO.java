package com.example.myproject.dto;

import java.time.LocalDate;
import java.util.Date;

import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.Nhom;
import lombok.Data;

@Data
public class SinhVienDeTaiDTO {
    private int maDT;
    private String tenDT;
    private String moTa;
    private LocalDate ngayBatDau;
    private LopTinChi lopTinChi;
    private Nhom nhom;
    private boolean completed;
    private Double diemTrungBinh;
    private boolean isMember; 
}