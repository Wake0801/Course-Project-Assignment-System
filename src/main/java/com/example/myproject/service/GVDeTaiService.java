package com.example.myproject.service;

import com.example.myproject.compositeKey.LoaiBaoCaoDeTaiId;
import com.example.myproject.dto.GiaoDeTaiForm;
import com.example.myproject.entity.*;
import com.example.myproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GVDeTaiService {

    @Autowired
    private DeTaiRepository deTaiRepository;
    
    @Autowired
    private LopTinChiRepository lopTinChiRepository;
    
    @Autowired
    private LoaiBaoCaoRepository loaiBaoCaoRepository;
    
    @Autowired
    private LBC_DTRepository lbcDtRepository;
    
    @Autowired
    private LBC_LTCRepository lbcLopTCRepository;
    
    @Autowired
    private NhomRepository nhomRepository;

    // Lấy danh sách đề tài theo bộ lọc
    public Page<DeTai> filterDeTai(String maLopTC, Integer maLoaiBaoCao, String keyword, Pageable pageable) {
        if (maLopTC != null && !maLopTC.isEmpty()) {
            if (maLoaiBaoCao != null) {
                // Lọc theo lớp tín chỉ và loại báo cáo
                return deTaiRepository.findByLopTinChi_MaLopTCAndLoaiBaoCao(maLopTC, maLoaiBaoCao, keyword, pageable);
            }
            return deTaiRepository.findByLopTinChi_MaLopTC(maLopTC, keyword, pageable);
        }
        return deTaiRepository.search(keyword, pageable);
    }

    // Lấy các loại báo cáo của lớp tín chỉ
    public List<LoaiBaoCao> getLoaiBaoCaoByLopTC(String maLopTC) {
        return lbcLopTCRepository.findByMaLopTC(maLopTC).stream()
                .map(LoaiBaoCaoLopTC::getLoaiBaoCao)
                .distinct()
                .collect(Collectors.toList());
    }

    // Thêm mới đề tài
    @Transactional
    public DeTai addDeTai(DeTai deTai, List<Integer> loaiBaoCaoIds) {
        // Kiểm tra lớp tín chỉ tồn tại
        LopTinChi lopTinChi = lopTinChiRepository.findById(deTai.getLopTinChi().getMaLopTC())
                .orElseThrow(() -> new RuntimeException("Lớp tín chỉ không tồn tại"));
        
        deTai.setLopTinChi(lopTinChi);
        DeTai savedDeTai = deTaiRepository.save(deTai);
        
        // Thêm các loại báo cáo cho đề tài
        for (Integer maLoaiBaoCao : loaiBaoCaoIds) {
            LoaiBaoCaoDeTai lbcDt = new LoaiBaoCaoDeTai();
            LoaiBaoCaoDeTaiId id = new LoaiBaoCaoDeTaiId();
            id.setMaDT(savedDeTai.getMaDT());
            id.setMaLoaiBaoCao(maLoaiBaoCao);
            lbcDt.setId(id);
            lbcDt.setDeTai(savedDeTai);
            lbcDt.setLoaiBaoCao(loaiBaoCaoRepository.findById(maLoaiBaoCao).orElseThrow());
            lbcDtRepository.save(lbcDt);
        }
        
        return savedDeTai;
    }

    @Transactional
    public boolean phanCong(GiaoDeTaiForm form) {
        List<Nhom> nhoms = nhomRepository.findNhomChuaCoDeTaiByLoaiBaoCao(form.getMaLopTC(), form.getMaLoaiBaoCao());
        List<DeTai> deTais = deTaiRepository.findDeTaiChuaCoNhomByLopTCAndLoaiBaoCao(form.getMaLopTC(), form.getMaLoaiBaoCao());

        Set<Integer> maNhomSet = new HashSet<>(form.getGiaoNhom().values());
        if (maNhomSet.size() < nhoms.size()) return false; // còn nhóm chưa gán

        for (Map.Entry<Integer, Integer> entry : form.getGiaoNhom().entrySet()) {
            Integer maDT = entry.getKey();
            Integer maNhom = entry.getValue();
            DeTai dt = deTaiRepository.findById(maDT).orElseThrow();
            Nhom nhom = nhomRepository.findById(maNhom).orElseThrow();
            dt.setNhom(nhom);
            deTaiRepository.save(dt);
        }
        return true;
    }

    // Chia ngẫu nhiên đề tài cho các nhóm
    @Transactional
    public boolean randomAssignDeTai(String maLopTC, Integer maLoaiBaoCao) {
        List<Nhom> nhoms = nhomRepository.findNhomChuaCoDeTaiByLoaiBaoCao(maLopTC, maLoaiBaoCao);
        List<DeTai> deTais = deTaiRepository.findDeTaiChuaCoNhomByLopTCAndLoaiBaoCao(maLopTC, maLoaiBaoCao);

        if (deTais.size() < nhoms.size()) return false;

        Collections.shuffle(deTais);
        for (int i = 0; i < nhoms.size(); i++) {
            DeTai dt = deTais.get(i);
            dt.setNhom(nhoms.get(i));
            deTaiRepository.save(dt);
        }
        return true;
    }

    // Nhập điểm cho các báo cáo
    @Transactional
    public void nhapDiem(List<LoaiBaoCaoDeTai> diemList) {
        for (LoaiBaoCaoDeTai diem : diemList) {
            // Validate điểm
            if (diem.getDiem() == null || diem.getDiem() < 0 || diem.getDiem() > 10) {
                throw new RuntimeException("Điểm phải từ 0 đến 10");
            }
            
            // Kiểm tra điểm chia hết cho 0.25
            double remainder = diem.getDiem() * 100 % 25;
            if (remainder != 0) {
                throw new RuntimeException("Điểm phải là bội số của 0.25");
            }
            
            // Kiểm tra nhóm đã được gán chưa
            if (diem.getDeTai().getNhom() == null) {
                throw new RuntimeException("Đề tài chưa được gán cho nhóm nào");
            }
            
            lbcDtRepository.save(diem);
        }
    }

    // Áp dụng ngày báo cáo cho tất cả
    @Transactional
    public void applyNgayBaoCaoForAll(LocalDate ngayBaoCao, List<LoaiBaoCaoDeTai> diemList) {
        for (LoaiBaoCaoDeTai diem : diemList) {
            diem.setNgayBaoCao(ngayBaoCao);
            lbcDtRepository.save(diem);
        }
    }
}