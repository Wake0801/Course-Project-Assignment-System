package com.example.myproject.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myproject.compositeKey.LoaiBaoCaoDeTaiId;
import com.example.myproject.compositeKey.LoaiBaoCaoLopTCId;
import com.example.myproject.dto.DeTaiDetailDTO;
import com.example.myproject.dto.SinhVienDeTaiDTO;
import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.LoaiBaoCao;
import com.example.myproject.entity.LoaiBaoCaoDeTai;
import com.example.myproject.entity.LoaiBaoCaoLopTC;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.repository.DeTaiRepository;
import com.example.myproject.repository.LBC_DTRepository;
import com.example.myproject.repository.LBC_LTCRepository;
import com.example.myproject.repository.LoaiBaoCaoRepository;
import com.example.myproject.repository.LopTinChiRepository;
import com.example.myproject.repository.NhomRepository;
import com.example.myproject.repository.SinhVienLTCRepository;
import com.example.myproject.repository.SinhVienNhomRepository;

@Service
public class SinhVienDeTaiService {
    
    @Autowired
    private SinhVienNhomRepository sinhVienNhomRepository;
    
    @Autowired
    private SinhVienLTCRepository sinhVienLTCRepository;
    
    @Autowired
    private DeTaiRepository deTaiRepository;
    
    @Autowired
    private NhomRepository nhomRepository;
    
    @Autowired
    private LBC_DTRepository lbcDTRepository; // Thay QuanLiDiemRepository
    @Autowired
    private LBC_LTCRepository lbcLTCRepository;
    @Autowired
    private LoaiBaoCaoRepository loaiBaoCaoRepository;
    
    @Autowired
    private LopTinChiRepository lopTinChiRepository;

    public List<LopTinChi> getLopTinChiBySinhVien(String maSV) {
        return sinhVienLTCRepository.findLopTinChiByMaSV(maSV);
    }
    
    public List<SinhVienDeTaiDTO> filterDeTai(String maSV, String maLopTC, String trangThai) {
        List<DeTai> deTais;
        
        if (maLopTC == null || maLopTC.isEmpty()) {
            List<LopTinChi> lopTinChis = getLopTinChiBySinhVien(maSV);
            List<String> maLopTCList = lopTinChis.stream()
                    .map(LopTinChi::getMaLopTC)
                    .collect(Collectors.toList());
            deTais = deTaiRepository.findByLopTinChi_MaLopTCIn(maLopTCList);
        } else {
            deTais = deTaiRepository.findByLopTinChi_MaLopTC(maLopTC);
        }
        
        return deTais.stream()
                .map(deTai -> {
                    SinhVienDeTaiDTO dto = convertToDTO(deTai, maSV);
                    if (trangThai != null && !trangThai.isEmpty()) {
                        boolean isCompleted = isDeTaiCompleted(deTai);
                        if (("completed".equals(trangThai) && isCompleted) || 
                            ("progress".equals(trangThai) && !isCompleted)) {
                            return dto;
                        }
                        return null;
                    }
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    private boolean isDeTaiCompleted(DeTai deTai) {
        List<LoaiBaoCao> loaiBaoCaos = loaiBaoCaoRepository.findAll();
        for (LoaiBaoCao loaiBaoCao : loaiBaoCaos) {
            LoaiBaoCaoDeTai lbc_dt = lbcDTRepository.findById(new LoaiBaoCaoDeTaiId(loaiBaoCao.getMaLoaiBaoCao(), deTai.getMaDT()))
                    .orElse(null);
            if (lbc_dt == null || lbc_dt.getDiem() == null || lbc_dt.getNgayBaoCao() == null) {
                return false;
            }
        }
        return true;
    }
    
    private SinhVienDeTaiDTO convertToDTO(DeTai deTai, String maSV) {
        SinhVienDeTaiDTO dto = new SinhVienDeTaiDTO();
        dto.setMaDT(deTai.getMaDT());
        dto.setTenDT(deTai.getTenDT());
        dto.setMoTa(deTai.getMoTa());
        dto.setNgayBatDau(deTai.getNgayBatDau());
        
        LopTinChi lopTinChi = lopTinChiRepository.findById(deTai.getLopTinChi().getMaLopTC()).orElse(null);
        dto.setLopTinChi(lopTinChi);
        
        // Lấy nhóm từ MaNhom trong DeTai
        Nhom nhom = deTai.getNhom();
        if (nhom != null) {
            dto.setNhom(nhom);
            boolean isMember = sinhVienNhomRepository.existsByMaSVAndMaNhom(maSV, nhom.getMaNhom());
            dto.setMember(isMember);
        }
        
        dto.setCompleted(isDeTaiCompleted(deTai));
        
        if (nhom != null) {
            List<LoaiBaoCaoDeTai> diems = lbcDTRepository.findById_MaDT(deTai.getMaDT());
            double tongDiem = 0;
            double tongHeSo = 0;
            
            for (LoaiBaoCaoDeTai diem : diems) {
                LoaiBaoCaoLopTC lbc_ltc = lbcLTCRepository.findById(new LoaiBaoCaoLopTCId(
                        diem.getId().getMaLoaiBaoCao(), deTai.getLopTinChi().getMaLopTC())).orElse(null);
                if (diem.getDiem() != null && lbc_ltc != null && lbc_ltc.getHeSoDiem() != null) {
                    tongDiem += diem.getDiem() * lbc_ltc.getHeSoDiem();
                    tongHeSo += lbc_ltc.getHeSoDiem();
                }
            }
            
            if (tongHeSo > 0) {
                dto.setDiemTrungBinh(Math.round((tongDiem / tongHeSo) * 10.0) / 10.0);
            }
        }
        
        return dto;
    }
    
    public DeTaiDetailDTO getDeTaiDetail(int maDT, int maNhom) {
        DeTai deTai = deTaiRepository.findById(maDT).orElseThrow();
        Nhom nhom = nhomRepository.findById(maNhom).orElseThrow();
        
        DeTaiDetailDTO dto = new DeTaiDetailDTO();
        dto.setDeTai(deTai);
        dto.setNhom(nhom);
        
        List<SinhVien> members = sinhVienNhomRepository.findByNhom(maNhom);
        dto.setMembers(members);
        
        List<LoaiBaoCaoDeTai> diems = lbcDTRepository.findById_MaDT(maDT);
        dto.setDiemSo(diems);
        
        LopTinChi lopTinChi = lopTinChiRepository.findById(deTai.getLopTinChi().getMaLopTC()).orElse(null);
        if (lopTinChi != null) {
            GiangVien giangVien = lopTinChi.getGiangVien();
            dto.setGiangVien(giangVien);
        }
        
        return dto;
    }
}