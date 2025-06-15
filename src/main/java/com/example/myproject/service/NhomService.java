package com.example.myproject.service;

import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.entity.SinhVienNhom;
import com.example.myproject.repository.NhomRepository;
import com.example.myproject.repository.SinhVienNhomRepository;
import com.example.myproject.repository.SinhVienRepository;
import com.example.myproject.repository.SinhVien_LTCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NhomService {
    @Autowired
    private NhomRepository nhomRepo;
    @Autowired
    private SinhVienNhomRepository svNhomRepo;
    @Autowired
    private SinhVienRepository svRepo;
    @Autowired
    private SinhVien_LTCRepository svLTCRepo;

    public List<Nhom> getAllNhom(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return nhomRepo.findAll();
        }
        // Tìm theo mã lớp TC
        return nhomRepo.findByMaLopTC(keyword);
    }

    public Nhom getNhom(Integer maNhom) {
        return nhomRepo.findById(maNhom).orElse(null);
    }

    public Nhom saveNhom(Nhom nhom) {
        return nhomRepo.save(nhom);
    }

    public void deleteNhom(Integer maNhom) {
        // Xóa tất cả sinh viên thuộc nhóm trước
        svNhomRepo.deleteAll(svNhomRepo.findByNhom_MaNhom(maNhom));
        nhomRepo.deleteById(maNhom);
    }

    public List<Map<String, Object>> getSinhVienInNhom(Integer maNhom) {
        List<SinhVienNhom> list = svNhomRepo.findByNhom_MaNhom(maNhom);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SinhVienNhom svNhom : list) {
            SinhVien sv = svNhom.getSinhVien();
            Map<String, Object> m = new HashMap<>();
            m.put("maSV", sv.getMaSV());
            m.put("ho", sv.getHo());
            m.put("ten", sv.getTen());
            m.put("gioiTinh", sv.isGioiTinh());
            m.put("ngayGiaNhap", svNhom.getNgayGiaNhap());
            m.put("ngayRoiNhom", svNhom.getNgayRoiNhom());
            result.add(m);
        }
        return result;
    }

    @Transactional
    public boolean addSinhVienToNhom(Integer maNhom, String maSV) {
        Nhom nhom = nhomRepo.findById(maNhom).orElse(null);
        SinhVien sv = svRepo.findById(maSV).orElse(null);
        if (nhom == null || sv == null) return false;
        // Không cho phép sinh viên thuộc nhiều nhóm khác nhau trong cùng 1 LTC
        List<Nhom> nhomsCungLTC = nhomRepo.findByMaLopTC(nhom.getMaLopTC());
        for (Nhom n : nhomsCungLTC) {
            if (svNhomRepo.existsByNhom_MaNhomAndSinhVien_MaSV(n.getMaNhom(), maSV)) {
                return false;
            }
        }
        SinhVienNhom svNhom = new SinhVienNhom();
        svNhom.setNhom(nhom);
        svNhom.setSinhVien(sv);
        svNhom.setNgayGiaNhap(new Date());
        svNhomRepo.save(svNhom);
        return true;
    }

    @Transactional
    public boolean removeSinhVienFromNhom(Integer maNhom, String maSV) {
        if (!svNhomRepo.existsByNhom_MaNhomAndSinhVien_MaSV(maNhom, maSV)) return false;
        svNhomRepo.deleteByNhom_MaNhomAndSinhVien_MaSV(maNhom, maSV);
        return true;
    }

    public Integer getNextMaNhom() {
        List<Nhom> all = nhomRepo.findAll();
        int max = 0;
        for (Nhom n : all) {
            if (n.getMaNhom() != null && n.getMaNhom() > max) max = n.getMaNhom();
        }
        return max + 1;
    }

    public boolean isSinhVienInLTC(String maLopTC, String maSV) {
        // Kiểm tra sinh viên có trong lớp tín chỉ không
        return svLTCRepo.findSinhViensByMaLopTC(maLopTC)
                .stream()
                .anyMatch(sv -> sv.getMaSV().equals(maSV));
    }
}
