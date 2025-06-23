package com.example.myproject.controller;

import com.example.myproject.dto.GiaoDeTaiForm;
import com.example.myproject.entity.*;
import com.example.myproject.repository.DeTaiRepository;
import com.example.myproject.repository.LopTinChiRepository;
import com.example.myproject.repository.NhomRepository;
import com.example.myproject.service.DeTaiService;
import com.example.myproject.service.GVDeTaiService;
import com.example.myproject.service.NhomService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/client/gv/topic")
public class GVDeTaiController {

    @Autowired
    private GVDeTaiService gVDeTaiService;
    @Autowired
    private LopTinChiRepository lopTinChiRepository;
    @Autowired
    private DeTaiRepository deTaiRepository ;
    @Autowired
    private NhomRepository nhomRepository;
    @GetMapping
    public String showDeTaiPage(
            @RequestParam(required = false) String maLopTC,
            @RequestParam(required = false) Integer maLoaiBaoCao,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            HttpSession session) {  // Thêm tham số HttpSession
        
        // Lấy mã giảng viên từ session
        String maGV = (String) session.getAttribute("maGV");
        
        if (maGV == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }
        
        // Lấy danh sách lớp tín chỉ của giảng viên
        List<LopTinChi> dsLopTinChi = lopTinChiRepository.findByGiangVien(maGV);
        
        // Lấy danh sách đề tài
        Page<DeTai> deTaiPage = gVDeTaiService.filterDeTai(
                maLopTC, 
                maLoaiBaoCao, 
                keyword, 
                PageRequest.of(page, size)
        );
        
        model.addAttribute("dsLopTinChi", dsLopTinChi);
        model.addAttribute("deTaiPage", deTaiPage);
        model.addAttribute("maLopTC", maLopTC);
        model.addAttribute("maLoaiBaoCao", maLoaiBaoCao);
        model.addAttribute("keyword", keyword);
        return "client/gv/topic";
    }

    @PostMapping("/add")
    public String addDeTai(
            @ModelAttribute DeTai deTai,
            @RequestParam List<Integer> loaiBaoCaoIds) {
        
        gVDeTaiService.addDeTai(deTai, loaiBaoCaoIds);
        return "redirect:/client/gv/topic?maLopTC=" + deTai.getLopTinChi().getMaLopTC();
    }
    @GetMapping("/assign")
    public String giaoDeTai(@RequestParam(required = false)  String maLopTC,
                            @RequestParam(required = false)  Integer maLoaiBaoCao,
                            Model model) {
        List<DeTai> deTais = deTaiRepository.findDeTaiChuaCoNhomByLopTCAndLoaiBaoCao(maLopTC, maLoaiBaoCao);

        List<Nhom> nhoms = nhomRepository.findNhomChuaCoDeTaiByLoaiBaoCao(maLopTC, maLoaiBaoCao);
        model.addAttribute("dsDeTai", deTais);
        model.addAttribute("dsNhom", nhoms);
        model.addAttribute("maLopTC", maLopTC);
        model.addAttribute("maLoaiBaoCao", maLoaiBaoCao);
        return "client/gv/topic"; // Fragment modal hoặc full page
    }

    @PostMapping("/assign")
    public String xacNhanGiaoDeTai(@RequestParam String maLopTC,
                                    @RequestParam Integer maLoaiBaoCao,
                                    @ModelAttribute GiaoDeTaiForm form,
                                    RedirectAttributes redirect) {
        boolean ok = gVDeTaiService.phanCong(form); // check & lưu
        if (!ok) {
            redirect.addFlashAttribute("error", "Có nhóm chưa có đề tài, không thể xác nhận.");
        } else {
            redirect.addFlashAttribute("success", "Giao đề tài thành công.");
        }
        return "redirect:/client/gv/topic?maLopTC=" + maLopTC + "&maLoaiBaoCao=" + maLoaiBaoCao;
    }

    @PostMapping("/random-assign")
    public String randomAssign(
            @RequestParam String maLopTC,
            @RequestParam Integer maLoaiBaoCao) {
        
        gVDeTaiService.randomAssignDeTai(maLopTC, maLoaiBaoCao);
        return "redirect:/client/gv/topic?maLopTC=" + maLopTC;
    }

    // @GetMapping("/diem")
    // public String nhapDiem(@RequestParam String maLopTC,
    //                     @RequestParam Integer maLoaiBaoCao,
    //                     Model model) {
    //     List<LoaiBaoCaoDeTai> ds = gVDeTaiService.getDeTaiDaPhanCong(maLopTC, maLoaiBaoCao);
    //     model.addAttribute("dsDiem", ds);
    //     model.addAttribute("maLopTC", maLopTC);
    //     model.addAttribute("maLoaiBaoCao", maLoaiBaoCao);
    //     return "client/gv/diem-modal"; // Fragment hoặc page riêng
    // }

    // @PostMapping("/diem")
    // public String luuDiem(@ModelAttribute DiemForm form,
    //                     RedirectAttributes redirect) {
    //     boolean ok = gVDeTaiService.luuDiem(form.getDanhSach());
    //     if (!ok) {
    //         redirect.addFlashAttribute("error", "Điểm không hợp lệ hoặc thiếu.");
    //     } else {
    //         redirect.addFlashAttribute("success", "Lưu điểm thành công.");
    //     }
    //     return "redirect:/client/gv/topic?maLopTC=" + form.getMaLopTC() + "&maLoaiBaoCao=" + form.getMaLoaiBaoCao();
    // }

}