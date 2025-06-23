package com.example.myproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myproject.dto.ThongKeDiemDTO;
import com.example.myproject.dto.ThongKeDiemDTO.ThanhVienNhomDTO;
import com.example.myproject.dto.ThongKePhanBoDiemDTO;
import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.LoaiBaoCao;
import com.example.myproject.entity.LoaiBaoCaoDeTai;
import com.example.myproject.entity.LoaiBaoCaoLopTC;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.repository.DeTaiRepository;
import com.example.myproject.repository.LBC_DTRepository;
import com.example.myproject.repository.LBC_LTCRepository;
import com.example.myproject.repository.LoaiBaoCaoRepository;
import com.example.myproject.repository.LopTinChiRepository;
import com.example.myproject.repository.SinhVienNhomRepository;

@Service
public class ThongKeService {
    
    @Autowired
    private LopTinChiRepository lopTinChiRepository;
    
    @Autowired
    private DeTaiRepository deTaiRepository;
    
    @Autowired
    private SinhVienNhomRepository sinhVienNhomRepository;
    
    @Autowired
    private LBC_DTRepository lbcDTRepository;
    
    @Autowired
    private LBC_LTCRepository lbcLTCRepository;
    
    @Autowired
    private LoaiBaoCaoRepository loaiBaoCaoRepository;
    
    public List<LopTinChi> getAllLopTinChi() {
        return lopTinChiRepository.findAll();
    }
    
    public List<ThongKeDiemDTO> getThongKeDiem(String maLopTC, String loaiCham, String searchKeyword) {
        if (maLopTC == null || maLopTC.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<DeTai> deTais = deTaiRepository.findByLopTinChi_MaLopTC(maLopTC);
        List<ThongKeDiemDTO> result = new ArrayList<>();
        
        for (DeTai deTai : deTais) {
            ThongKeDiemDTO dto = new ThongKeDiemDTO();
            dto.setMaDT("DT" + String.format("%03d", deTai.getMaDT()));
            dto.setTenDT(deTai.getTenDT());
            dto.setTenNhom(deTai.getNhom().getTenNhom());
            
            // Lấy điểm của đề tài
            List<LoaiBaoCaoDeTai> diemList = lbcDTRepository.findById_MaDT(deTai.getMaDT());
            Map<Integer, Double> diemMap = diemList.stream()
                .filter(d -> d.getDiem() != null)
                .collect(Collectors.toMap(
                    d -> d.getId().getMaLoaiBaoCao(), 
                    LoaiBaoCaoDeTai::getDiem
                ));
            
            // Xác định loại chấm điểm của đề tài
            String loaiChamDeTai = xacDinhLoaiCham(deTai, diemMap);
            dto.setLoaiCham(loaiChamDeTai);
            
            // Set điểm theo loại chấm được chọn
            setDiemByLoaiChamSelected(dto, diemMap, loaiCham, loaiChamDeTai);
            
            // Lấy danh sách thành viên nhóm
            List<SinhVien> thanhViens = sinhVienNhomRepository.findByNhom(deTai.getNhom().getMaNhom());
            List<ThanhVienNhomDTO> thanhVienDTOs = new ArrayList<>();
            
            for (SinhVien sv : thanhViens) {
                ThanhVienNhomDTO tvDto = new ThanhVienNhomDTO();
                tvDto.setMaSV(sv.getMaSV());
                tvDto.setTenSV(sv.getHo() + " " + sv.getTen());
                tvDto.setLopSV(sv.getLop().getMaLop()); // Thêm thông tin lớp
                tvDto.setDiemGiuaKy(dto.getDiemGiuaKy());
                tvDto.setDiemCuoiKy(dto.getDiemCuoiKy());
                tvDto.setDiemTongKet(dto.getDiemTongKet());
                thanhVienDTOs.add(tvDto);
            }
            
            dto.setThanhViens(thanhVienDTOs);
            
            // Lọc theo từ khóa tìm kiếm
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String keyword = searchKeyword.toLowerCase().trim();
                boolean match = dto.getMaDT().toLowerCase().contains(keyword) ||
                               dto.getTenDT().toLowerCase().contains(keyword) ||
                               dto.getTenNhom().toLowerCase().contains(keyword) ||
                               thanhVienDTOs.stream().anyMatch(tv -> 
                                   tv.getMaSV().toLowerCase().contains(keyword) ||
                                   tv.getTenSV().toLowerCase().contains(keyword)
                               );
                if (!match) {
                    continue;
                }
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    private String xacDinhLoaiCham(DeTai deTai, Map<Integer, Double> diemMap) {
        // Giả sử: mã 1 = giữa kỳ, mã 2 = cuối kỳ
        boolean coGiuaKy = diemMap.containsKey(1);
        boolean coCuoiKy = diemMap.containsKey(2);
        
        if (coGiuaKy && coCuoiKy) {
            return "both";
        } else if (coGiuaKy) {
            return "mid";
        } else if (coCuoiKy) {
            return "final";
        } else {
            return "none";
        }
    }
    
    private void setDiemByLoaiChamSelected(ThongKeDiemDTO dto, Map<Integer, Double> diemMap, String loaiChamSelected, String loaiChamDeTai) {
        // Xóa tất cả điểm trước
        dto.setDiemGiuaKy(null);
        dto.setDiemCuoiKy(null);
        dto.setDiemTongKet(null);
        
        if (loaiChamSelected == null || loaiChamSelected.isEmpty()) {
            // Nếu không chọn loại chấm, hiển thị theo logic cũ
            setDiemByLoaiCham(dto, diemMap, loaiChamDeTai);
            return;
        }
        
        switch (loaiChamSelected) {
            case "mid":
                // Chỉ hiển thị điểm giữa kì nếu có
                if (diemMap.containsKey(1)) {
                    dto.setDiemGiuaKy(diemMap.get(1));
                }
                break;
            case "final":
                // Chỉ hiển thị điểm cuối kì nếu có
                if (diemMap.containsKey(2)) {
                    dto.setDiemCuoiKy(diemMap.get(2));
                }
                break;
            case "both":
                // Hiển thị tổng điểm
                Double giuaKy = diemMap.get(1);
                Double cuoiKy = diemMap.get(2);
                if (giuaKy != null && cuoiKy != null) {
                    dto.setDiemTongKet((giuaKy + cuoiKy) / 2.0);
                } else if (giuaKy != null) {
                    dto.setDiemTongKet(giuaKy);
                } else if (cuoiKy != null) {
                    dto.setDiemTongKet(cuoiKy);
                }
                break;
        }
    }
    
    private void setDiemByLoaiCham(ThongKeDiemDTO dto, Map<Integer, Double> diemMap, String loaiCham) {
        switch (loaiCham) {
            case "both":
                // Nếu có cả 2 điểm, chỉ hiển thị cuối kỳ
                dto.setDiemCuoiKy(diemMap.get(2));
                dto.setDiemTongKet(diemMap.get(2));
                break;
            case "mid":
                dto.setDiemGiuaKy(diemMap.get(1));
                dto.setDiemTongKet(diemMap.get(1));
                break;
            case "final":
                dto.setDiemCuoiKy(diemMap.get(2));
                dto.setDiemTongKet(diemMap.get(2));
                break;
            case "none":
                // Có thể có điểm từ loại khác
                if (!diemMap.isEmpty()) {
                    Double firstScore = diemMap.values().iterator().next();
                    dto.setDiemTongKet(firstScore);
                }
                break;
        }
    }
    
    public LopTinChi getLopTinChiById(String maLopTC) {
        return lopTinChiRepository.findById(maLopTC).orElse(null);
    }
    
    public ThongKePhanBoDiemDTO getPhanBoDiem(String maLopTC, String loaiCham) {
        if (maLopTC == null || maLopTC.isEmpty()) {
            return null;
        }
        
        // Lấy thông tin lớp tín chỉ
        LopTinChi lopTinChi = getLopTinChiById(maLopTC);
        if (lopTinChi == null) {
            return null;
        }
        
        // Lấy danh sách điểm của tất cả sinh viên trong lớp
        List<ThongKeDiemDTO> thongKeDiem = getThongKeDiem(maLopTC, loaiCham, null);
        
        // Tính toán phân bố điểm
        List<Double> danhSachDiem = new ArrayList<>();
        for (ThongKeDiemDTO dto : thongKeDiem) {
            for (ThongKeDiemDTO.ThanhVienNhomDTO tv : dto.getThanhViens()) {
                Double diem = null;
                if ("mid".equals(loaiCham)) {
                    diem = tv.getDiemGiuaKy();
                } else if ("final".equals(loaiCham)) {
                    diem = tv.getDiemCuoiKy();
                } else if ("both".equals(loaiCham)) {
                    diem = tv.getDiemTongKet();
                } else {
                    // Mặc định lấy điểm tổng kết
                    diem = tv.getDiemTongKet();
                }
                
                if (diem != null) {
                    danhSachDiem.add(diem);
                }
            }
        }
        
        // Tạo DTO kết quả
        ThongKePhanBoDiemDTO result = new ThongKePhanBoDiemDTO();
        result.setMaLopTC(maLopTC);
        result.setTenLopTC(lopTinChi.getMonHoc().getTenMon());
        result.setLoaiCham(loaiCham);
        result.setTongSinhVien(danhSachDiem.size());
        
        // Tính phân bố theo các khoảng điểm
        List<ThongKePhanBoDiemDTO.KhoangDiemDTO> phanBoDiem = tinhPhanBoDiem(danhSachDiem);
        result.setPhanBoDiem(phanBoDiem);
        
        return result;
    }
    
    private List<ThongKePhanBoDiemDTO.KhoangDiemDTO> tinhPhanBoDiem(List<Double> danhSachDiem) {
        int tongSinhVien = danhSachDiem.size();
        List<ThongKePhanBoDiemDTO.KhoangDiemDTO> phanBoDiem = new ArrayList<>();
        
        // Định nghĩa các khoảng điểm
        String[] khoangDiem = {"9.0-10.0", "8.0-8.9", "7.0-7.9", "6.0-6.9", "5.0-5.9", "Dưới 5.0"};
        double[][] ranges = {{9.0, 10.0}, {8.0, 8.9}, {7.0, 7.9}, {6.0, 6.9}, {5.0, 5.9}, {0.0, 4.9}};
        
        for (int i = 0; i < khoangDiem.length; i++) {
            int count = 0;
            for (Double diem : danhSachDiem) {
                if (i == ranges.length - 1) {
                    // Khoảng "Dưới 5.0"
                    if (diem < 5.0) {
                        count++;
                    }
                } else {
                    // Các khoảng khác
                    if (diem >= ranges[i][0] && diem <= ranges[i][1]) {
                        count++;
                    }
                }
            }
            
            double tyLe = tongSinhVien > 0 ? (double) count / tongSinhVien * 100 : 0;
            phanBoDiem.add(new ThongKePhanBoDiemDTO.KhoangDiemDTO(khoangDiem[i], count, Math.round(tyLe * 100.0) / 100.0));
        }
        
        return phanBoDiem;
    }
} 