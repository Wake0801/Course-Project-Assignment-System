package com.example.myproject.controller;

import com.example.myproject.entity.DeTai;
import com.example.myproject.service.DeTaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/topics")
public class DeTaiController {

    @Autowired
    private DeTaiService deTaiService;

    @GetMapping("")
    public String manageTopics(Model model,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String filterKhoa,
                             @RequestParam(required = false) String filterGiangVien) {

        Page<DeTai> deTaiPage = deTaiService.findAllDeTai(page, size, keyword, filterKhoa, filterGiangVien);
        
        model.addAttribute("deTaiList", deTaiPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", deTaiPage.getTotalPages());
        model.addAttribute("totalItems", deTaiPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        
        // Add filter data
        model.addAttribute("khoaList", deTaiService.getAllKhoa());
        model.addAttribute("giangVienList", deTaiService.getAllGiangVien());
        
        // Add selected filter values
        model.addAttribute("filterKhoa", filterKhoa);
        model.addAttribute("filterGiangVien", filterGiangVien);
        
        if (!model.containsAttribute("deTai")) {
            model.addAttribute("deTai", new DeTai());
        }
        
        return "admin/manageTopics";
    }

    @GetMapping("/add")
    public String showAddTopicForm(Model model) {
        model.addAttribute("deTai", new DeTai());
        model.addAttribute("khoaList", deTaiService.getAllKhoa());
        model.addAttribute("giangVienList", deTaiService.getAllGiangVien());
        return "admin/manageTopics";
    }

    @GetMapping("/edit/{maDT}")
    public String showEditTopicForm(@PathVariable String maDT, Model model, RedirectAttributes redirectAttributes) {
        Optional<DeTai> deTaiOpt = deTaiService.findById(maDT);
        if (deTaiOpt.isPresent()) {
            model.addAttribute("deTai", deTaiOpt.get());
            model.addAttribute("khoaList", deTaiService.getAllKhoa());
            model.addAttribute("giangVienList", deTaiService.getAllGiangVien());
            return "admin/manageTopics";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đề tài với mã: " + maDT);
        return "redirect:/topics";
    }

    @PostMapping("/save")
    public String saveTopic(@ModelAttribute DeTai deTai, RedirectAttributes redirectAttributes) {
        try {
            deTaiService.save(deTai);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu đề tài thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu đề tài: " + e.getMessage());
        }
        return "redirect:/topics";
    }

    @GetMapping("/delete/{maDT}")
    public String deleteTopic(@PathVariable String maDT, RedirectAttributes redirectAttributes) {
        try {
            deTaiService.deleteById(maDT);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đề tài thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa đề tài: " + e.getMessage());
        }
        return "redirect:/topics";
    }
}