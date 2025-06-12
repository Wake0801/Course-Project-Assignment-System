package com.example.myproject.dto;

import java.util.List;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.LoaiBaoCaoDeTai;
import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.SinhVien;

import lombok.Data;

@Data
public class DeTaiDetailDTO {
    private DeTai deTai;
    private Nhom nhom;
    private List<SinhVien> members;
    private List<LoaiBaoCaoDeTai> diemSo;
    private GiangVien giangVien;
}