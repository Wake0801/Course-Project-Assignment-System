package com.example.myproject.controller;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.myproject.dto.NhomDTO;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.Nhom;
import com.example.myproject.repository.LopTinChiRepository;
import com.example.myproject.service.ClientNhomService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/client/public")
public class ClientNhomController {
    @Autowired
    private ClientNhomService nhomService;
    
    @Autowired
    private LopTinChiRepository lopTinChiRepository;
    
    @GetMapping("/studentGroup")
    public String showStudentGroupPage(Model model, 
                                    @RequestParam(required = false) String maLopTC,
                                    Authentication authentication,
                                    HttpSession session) {
        
        // Lấy danh sách lớp tín chỉ theo vai trò
        List<LopTinChi> dsLopTC = getLopTinChiTheoVaiTro(authentication, session);
        model.addAttribute("dsLopTC", dsLopTC);
        
        if (maLopTC != null && !maLopTC.isEmpty()) {
            List<NhomDTO> dsNhom = nhomService.getNhomByLopTC(maLopTC);
            model.addAttribute("dsNhom", dsNhom);
            model.addAttribute("selectedLopTC", maLopTC);
            model.addAttribute("daCoNhom", !dsNhom.isEmpty());
            
            if (isSinhVien(authentication)) {
                String maSV = (String) session.getAttribute("maSV");
                Optional<NhomDTO> currentNhom = dsNhom.stream()
                    .filter(n -> n.getThanhVien().stream()
                        .anyMatch(sv -> sv.getSinhVien().getMaSV().equals(maSV) && sv.getNgayRoiNhom() == null))
                    .findFirst();
                model.addAttribute("daThamGiaNhom", currentNhom.isPresent());
                model.addAttribute("currentNhom", currentNhom.orElse(null));
            }
        }
        
        return "client/public/studentGroup";
    }

    private List<LopTinChi> getLopTinChiTheoVaiTro(Authentication authentication, HttpSession session) {
        if (authentication != null) {
            if (isSinhVien(authentication)) {
                String maSV = (String) session.getAttribute("maSV");
                return lopTinChiRepository.findBySinhVien(maSV);
            } else if (isGiangVien(authentication)) {
                String maGV = (String) session.getAttribute("maGV");
                return lopTinChiRepository.findByGiangVien(maGV);
            }
        }
        return Collections.emptyList();
    }

    private boolean isSinhVien(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_SINH_VIEN"));
    }

    private boolean isGiangVien(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_GIANG_VIEN"));
    }
    @PostMapping("/studentGroup/taoNhomNgauNhien")
    @PreAuthorize("hasRole('GIANG_VIEN')")
    public String taoNhomNgauNhien(@RequestParam String maLopTC,
                                 @RequestParam int soNhom,
                                 @RequestParam int soLuongTVToiDa,
                                 RedirectAttributes redirectAttributes) {
        try {
            nhomService.taoNhomNgauNhien(maLopTC, soNhom, soLuongTVToiDa);
            redirectAttributes.addFlashAttribute("success", "Tạo nhóm ngẫu nhiên thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/client/public/studentGroup?maLopTC=" + maLopTC;
    }
    
    @PostMapping("/studentGroup/taoFormDangKy")
    @PreAuthorize("hasRole('GIANG_VIEN')")
    public String taoFormDangKy(@RequestParam String maLopTC,
                              @RequestParam int soNhom,
                              @RequestParam int soLuongTVToiDa,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayHetHan,
                              RedirectAttributes redirectAttributes) {
        try {
            nhomService.taoFormDangKyNhom(maLopTC, soNhom, soLuongTVToiDa, ngayHetHan);
            redirectAttributes.addFlashAttribute("success", "Tạo form đăng ký nhóm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/client/public/studentGroup?maLopTC=" + maLopTC;
    }
    
    @PostMapping("/studentGroup/dangKyNhom")
    @PreAuthorize("hasRole('SINH_VIEN')")
    public String dangKyNhom(@RequestParam int maNhom,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes,
                           @RequestParam String maLopTC) {
        try {
            String maSV = authentication.getName();
            nhomService.dangKyNhom(maSV, maNhom);
            redirectAttributes.addFlashAttribute("success", "Đăng ký nhóm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/client/public/studentGroup?maLopTC=" + maLopTC;
    }
    
    @PostMapping("/studentGroup/roiNhom")
    @PreAuthorize("hasRole('SINH_VIEN')")
    public String roiNhom(@RequestParam int maNhom,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes,
                        @RequestParam String maLopTC) {
        try {
            String maSV = authentication.getName();
            nhomService.roiNhom(maSV, maNhom);
            redirectAttributes.addFlashAttribute("success", "Rời nhóm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/client/public/studentGroup?maLopTC=" + maLopTC;
    }
}
