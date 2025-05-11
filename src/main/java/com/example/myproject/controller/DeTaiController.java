package com.example.myproject.controller;

import com.example.myproject.entity.DeTai;
import com.example.myproject.service.DeTaiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/topics")
public class DeTaiController {

    @Autowired
    private DeTaiService deTaiService;

    @GetMapping("")
    public String manageTopics(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "filterKhoa", required = false) String filterKhoa,
        @RequestParam(value = "filterGiangVien", required = false) String filterGiangVien,
        Model model
    ) {
        Page<DeTai> deTaiPage = deTaiService.findAllDeTai(page, size, keyword, filterKhoa, filterGiangVien);
        
        if (!model.containsAttribute("editTopic")) {
            model.addAttribute("editTopic", new DeTai());
        }
        
        model.addAttribute("deTaiList", deTaiPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", deTaiPage.getTotalPages());
        model.addAttribute("totalItems", deTaiPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        
        // Add filter data
        model.addAttribute("khoaList", deTaiService.getAllKhoa());
        model.addAttribute("giangVienList", deTaiService.getAllGiangVien());
        model.addAttribute("lopTCList", deTaiService.getAllLopTinChi());
        
        // Add selected filter values
        model.addAttribute("filterKhoa", filterKhoa);
        model.addAttribute("filterGiangVien", filterGiangVien);
        
        return "admin/manageTopics";
    }

    @GetMapping("/edit/{maDT}")
    public String showEditTopicForm(@PathVariable String maDT, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<DeTai> deTaiOpt = deTaiService.findById(maDT);
            if (deTaiOpt.isPresent()) {
                model.addAttribute("editTopic", deTaiOpt.get());
                model.addAttribute("khoaList", deTaiService.getAllKhoa());
                model.addAttribute("giangVienList", deTaiService.getAllGiangVien());
                model.addAttribute("lopTCList", deTaiService.getAllLopTinChi());
                
                return "redirect:/topics";
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đề tài với mã: " + maDT);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tìm đề tài: " + e.getMessage());
        }
        return "redirect:/topics";
    }

    @PostMapping("/save")
    public String saveTopic(@ModelAttribute @Valid DeTai editTopic, 
                           BindingResult result, 
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editTopic", result);
            redirectAttributes.addFlashAttribute("editTopic", editTopic);
            return "redirect:/topics";
        }
        
        try {
            deTaiService.save(editTopic);
            redirectAttributes.addFlashAttribute("success", "Lưu đề tài thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Dữ liệu đề tài vi phạm ràng buộc toàn vẹn");
            redirectAttributes.addFlashAttribute("editTopic", editTopic);
            return "redirect:/topics";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu đề tài: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editTopic", editTopic);
            return "redirect:/topics";
        }
        return "redirect:/topics";
    }

    @GetMapping("/delete/{maDT}")
    public String deleteTopic(@PathVariable String maDT, RedirectAttributes redirectAttributes) {
        try {
            deTaiService.deleteById(maDT);
            redirectAttributes.addFlashAttribute("success", "Xóa đề tài thành công!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đề tài để xóa!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa đề tài vì có dữ liệu liên quan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa đề tài: " + e.getMessage());
        }
        return "redirect:/topics";
    }
}