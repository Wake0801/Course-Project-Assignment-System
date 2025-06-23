package com.example.myproject.dto;

import java.util.List;
import lombok.Data;

@Data
public class ThongKeDiemDTO {
    private String maDT;
    private String tenDT;
    private String tenNhom;
    private List<ThanhVienNhomDTO> thanhViens;
    private Double diemGiuaKy;
    private Double diemCuoiKy;
    private Double diemTongKet;
    private String loaiCham; // "both", "mid", "final", "none"
    private boolean showDetail;
    
    @Data
    public static class ThanhVienNhomDTO {
        private String maSV;
        private String tenSV;
        private String lopSV; // Thêm thông tin lớp của sinh viên
        private Double diemGiuaKy;
        private Double diemCuoiKy;
        private Double diemTongKet;
    }
} 