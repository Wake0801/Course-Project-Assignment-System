package com.example.myproject.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myproject.compositeKey.SinhVienNhomId;
import com.example.myproject.dto.NhomDTO;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.SinhVienNhom;
import com.example.myproject.entity.SinhVien_LTC;
import com.example.myproject.repository.LopTinChiRepository;
import com.example.myproject.repository.NhomRepository;
import com.example.myproject.repository.SinhVienLTCRepository;
import com.example.myproject.repository.SinhVienNhomRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ClientNhomService {
    @Autowired
    private NhomRepository nhomRepository;
    
    @Autowired
    private SinhVienNhomRepository sinhVienNhomRepository;
    
    @Autowired
    private SinhVienLTCRepository sinhVienLTCRepository;
    
    @Autowired
    private LopTinChiRepository lopTinChiRepository;
    
    // Tạo nhóm ngẫu nhiên
    public List<Nhom> taoNhomNgauNhien(String maLopTC, int soNhom, int soLuongTVToiDa) {
        // Kiểm tra điều kiện
        int soSinhVien = sinhVienLTCRepository.countByLopTinChi_MaLopTC(maLopTC);
        if (soNhom > soSinhVien / 2) {
            throw new IllegalArgumentException("Số nhóm không được vượt quá số sinh viên/2");
        }
        if (soLuongTVToiDa < 2) {
            throw new IllegalArgumentException("Số lượng thành viên tối thiểu là 2");
        }
        
        // Lấy danh sách sinh viên trong lớp tín chỉ
        List<SinhVien_LTC> dsSinhVien = sinhVienLTCRepository.findByLopTinChi_MaLopTC(maLopTC);
        Collections.shuffle(dsSinhVien); // Xáo trộn ngẫu nhiên
        
        // Tạo các nhóm
        List<Nhom> nhoms = new ArrayList<>();
        LopTinChi lopTinChi = lopTinChiRepository.findById(maLopTC)
            .orElseThrow(() -> new EntityNotFoundException("Lớp tín chỉ không tồn tại"));
        
        for (int i = 0; i < soNhom; i++) {
            Nhom nhom = new Nhom();
            nhom.setTenNhom("Nhóm " + (i + 1));
            nhom.setSoLuongTVToiDa(soLuongTVToiDa);
            nhom.setLopTinChi(lopTinChi);
            nhom.setNgayLapNhom(LocalDate.now());
            nhoms.add(nhom);
        }
        
        // Lưu các nhóm vào database trước
        nhoms = nhomRepository.saveAll(nhoms);
        
        // Phân chia sinh viên vào các nhóm
        int currentNhomIndex = 0;
        for (SinhVien_LTC sv : dsSinhVien) {
            Nhom currentNhom = nhoms.get(currentNhomIndex);
            
            SinhVienNhom svNhom = new SinhVienNhom();
            SinhVienNhomId id = new SinhVienNhomId();
            id.setMaSV(sv.getSinhVien().getMaSV());
            id.setMaNhom(currentNhom.getMaNhom());
            svNhom.setSinhVienNhomId(id);
            svNhom.setSinhVien(sv.getSinhVien());
            svNhom.setNhom(currentNhom);
            svNhom.setNgayGiaNhap(LocalDate.now());
            
            sinhVienNhomRepository.save(svNhom);
            
            currentNhomIndex = (currentNhomIndex + 1) % soNhom;
        }
        
        return nhoms;
    }
    
    // Tạo form đăng ký nhóm (hình thức 2)
    public List<Nhom> taoFormDangKyNhom(String maLopTC, int soNhom, int soLuongTVToiDa, LocalDate ngayHetHan) {
        // Kiểm tra điều kiện
        int soSinhVien = sinhVienLTCRepository.countByLopTinChi_MaLopTC(maLopTC);
        if (soNhom > soSinhVien / 2) {
            throw new IllegalArgumentException("Số nhóm không được vượt quá số sinh viên/2");
        }
        if (soLuongTVToiDa < 2) {
            throw new IllegalArgumentException("Số lượng thành viên tối thiểu là 2");
        }
        
        LopTinChi lopTinChi = lopTinChiRepository.findById(maLopTC)
            .orElseThrow(() -> new EntityNotFoundException("Lớp tín chỉ không tồn tại"));
        
        List<Nhom> nhoms = new ArrayList<>();
        for (int i = 0; i < soNhom; i++) {
            Nhom nhom = new Nhom();
            nhom.setTenNhom("Nhóm " + (i + 1));
            nhom.setSoLuongTVToiDa(soLuongTVToiDa);
            nhom.setLopTinChi(lopTinChi);
            nhom.setNgayLapNhom(LocalDate.now());
            nhom.setNgayDongDangKyNhom(ngayHetHan);
            nhoms.add(nhom);
        }
        
        return nhomRepository.saveAll(nhoms);
    }
    
    // Đăng ký nhóm cho sinh viên
    public void dangKyNhom(String maSV, int maNhom) {
        // Kiểm tra xem nhóm có tồn tại không
        Nhom nhom = nhomRepository.findById(maNhom)
            .orElseThrow(() -> new EntityNotFoundException("Nhóm không tồn tại"));
        
        
        // Kiểm tra xem sinh viên đã ở trong nhóm nào của lớp tín chỉ này chưa
        if (sinhVienNhomRepository.existsBySinhVien_MaSVAndNhom_LopTinChi_MaLopTCAndNgayRoiNhomIsNull(maSV, nhom.getLopTinChi().getMaLopTC())) {
            throw new IllegalArgumentException("Sinh viên đã ở trong một nhóm khác của lớp tín chỉ này");
        }
        
        // Kiểm tra xem nhóm đã đầy chưa
        int currentMembers = nhomRepository.countCurrentMembersInNhom(maNhom);
        if (currentMembers >= nhom.getSoLuongTVToiDa()) {
            throw new IllegalArgumentException("Nhóm đã đầy");
        }
        
        // Kiểm tra xem đã hết hạn đăng ký chưa
        if (nhom.getNgayDongDangKyNhom() != null && LocalDate.now().isAfter(nhom.getNgayDongDangKyNhom())) {
            throw new IllegalArgumentException("Đã hết hạn đăng ký nhóm");
        }
        
        // Thêm sinh viên vào nhóm
        SinhVienNhom svNhom = new SinhVienNhom();
        SinhVienNhomId id = new SinhVienNhomId();
        id.setMaSV(maSV);
        id.setMaNhom(maNhom);
        svNhom.setSinhVienNhomId(id);
        svNhom.setNgayGiaNhap(LocalDate.now());
        
        sinhVienNhomRepository.save(svNhom);
    }
    
    // Rời nhóm
    public void roiNhom(String maSV, int maNhom) {
        SinhVienNhomId id = new SinhVienNhomId();
        id.setMaSV(maSV);
        id.setMaNhom(maNhom);
        
        SinhVienNhom svNhom = sinhVienNhomRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Sinh viên không ở trong nhóm này"));
        
        if (svNhom.getNgayRoiNhom() != null) {
            throw new IllegalArgumentException("Sinh viên đã rời nhóm trước đó");
        }
        
        svNhom.setNgayRoiNhom(LocalDate.now());
        sinhVienNhomRepository.save(svNhom);
    }
    
    // Lấy danh sách nhóm theo lớp tín chỉ
    public List<NhomDTO> getNhomByLopTC(String maLopTC) {
    List<Nhom> nhoms = nhomRepository.findByLopTinChi_MaLopTC(maLopTC);
    return nhoms.stream().map(nhom -> {
        List<SinhVienNhom> thanhVien = sinhVienNhomRepository.findByNhom_MaNhom(nhom.getMaNhom());
        return new NhomDTO(nhom, thanhVien);
    }).collect(Collectors.toList());
}
    // Lấy thông tin nhóm hiện tại của sinh viên trong lớp tín chỉ
    public Optional<Nhom> getCurrentNhomOfSinhVienInLopTC(String maSV, String maLopTC) {
        Optional<SinhVienNhom> svNhom = sinhVienNhomRepository.findCurrentNhomOfSinhVienInLopTC(maSV, maLopTC);
        return svNhom.map(SinhVienNhom::getNhom);
    }
}
