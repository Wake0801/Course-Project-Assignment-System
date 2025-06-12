package com.example.myproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myproject.service.SinhVienDeTaiService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/client/sv")
public class ClientSVController {
    
    @Autowired
    private SinhVienDeTaiService sinhVienDeTaiService;
    
    @GetMapping("/deTai")
    public String viewDeTai(Model model, 
                          @RequestParam(required = false) String maLopTC,
                          @RequestParam(required = false) String trangThai,
                          HttpSession session) {
        
        String maSV = (String) session.getAttribute("maSV");
        System.out.println(maSV);
        // Lấy danh sách lớp tín chỉ của sinh viên cho combobox
        model.addAttribute("lopTinChis", sinhVienDeTaiService.getLopTinChiBySinhVien(maSV));
        System.out.println(sinhVienDeTaiService.getLopTinChiBySinhVien(maSV));
        // Lọc đề tài
        model.addAttribute("deTais", sinhVienDeTaiService.filterDeTai(maSV, maLopTC, trangThai));
        
        return "client/sv/deTai";
    }
    
    @GetMapping("/deTai/detail")
    public String viewDeTaiDetail(@RequestParam int maDT, 
                                @RequestParam int maNhom,
                                Model model) {
        
        model.addAttribute("detail", sinhVienDeTaiService.getDeTaiDetail(maDT, maNhom));
        return "client/sv/deTaiDetail";
    }
}