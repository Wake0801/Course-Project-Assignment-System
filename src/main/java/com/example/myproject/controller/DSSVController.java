package com.example.myproject.controller;

import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.repository.LopTinChiRepository;
import com.example.myproject.service.DSSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/client/gv/")
public class DSSVController {

    @Autowired
    private DSSVService dssvService;

    @Autowired
    private LopTinChiRepository lopTinChiRepository;

    @GetMapping("studentList")
    public String showStudentList(
            @RequestParam(name = "maLopTC", required = false) String maLopTC,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        
        // Lấy danh sách lớp tín chỉ cho dropdown
        List<LopTinChi> dsLopTinChi = lopTinChiRepository.findAll();
        model.addAttribute("dsLopTinChi", dsLopTinChi);

        if (maLopTC != null && !maLopTC.isEmpty()) {
            // Lấy danh sách sinh viên theo lớp tín chỉ
            Page<SinhVien> sinhVienPage = dssvService.getSinhVienByLopTinChi(
                    maLopTC, 
                    PageRequest.of(page, size)
            );
            
            // Lấy thông tin lớp tín chỉ đang chọn
            Optional<LopTinChi> lopTinChi = lopTinChiRepository.findByMaLopTC(maLopTC);
            
            model.addAttribute("sinhVienPage", sinhVienPage);
            model.addAttribute("selectedLopTC", lopTinChi.orElse(null));
            model.addAttribute("maLopTC", maLopTC);
        }

        return "/client/gv/studentList";
    }
}