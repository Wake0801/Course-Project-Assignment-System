package com.example.myproject.dto;

import lombok.Data;
import java.util.List;

@Data
public class ThongKePhanBoDiemDTO {
    private String maLopTC;
    private String tenLopTC;
    private String loaiCham; // "mid", "final", "both"
    private int tongSinhVien;
    private List<KhoangDiemDTO> phanBoDiem;
    
    @Data
    public static class KhoangDiemDTO {
        private String khoangDiem; // "9.0-10.0", "8.0-8.9", etc.
        private int soLuongSinhVien;
        private double tyLe; // % 
        
        public KhoangDiemDTO(String khoangDiem, int soLuongSinhVien, double tyLe) {
            this.khoangDiem = khoangDiem;
            this.soLuongSinhVien = soLuongSinhVien;
            this.tyLe = tyLe;
        }
    }
} 