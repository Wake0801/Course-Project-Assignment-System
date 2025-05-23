package com.example.myproject.controller;

import com.example.myproject.entity.Nhom;
import com.example.myproject.service.NhomService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/groups") 
public class NhomController {

    @Autowired
    private NhomService nhomService;

    @GetMapping
    public String listGroups(
        @RequestParam(value = "searchKeyword", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        Model model
    ) {
        Page<Nhom> nhomPage = nhomService.findNhom(keyword, page, size);
        
        if (!model.containsAttribute("editNhom")) {
            model.addAttribute("editNhom", new Nhom());
            model.addAttribute("showEditModal", false);
        }
        
        model.addAttribute("ListGroups", nhomPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", nhomPage.getTotalPages());
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("size", size);
        
        return "admin/manageGroups";
    }

    @PostMapping("/save")
    public String saveGroup(
        @Valid @ModelAttribute("editNhom") Nhom nhom,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editNhom", result);
            redirectAttributes.addFlashAttribute("editNhom", nhom);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/groups";
        }
        
        try {
            nhomService.save(nhom);
            redirectAttributes.addFlashAttribute("success", "Lưu nhóm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editNhom", nhom);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            return "redirect:/groups";
        }
        
        return "redirect:/groups";
    }

    @GetMapping("/delete/{id}")
    public String deleteGroup(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            nhomService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa nhóm thành công");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhóm với ID: " + id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/groups";
    }

    @GetMapping("/edit/{id}")
    public String editGroup(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        return nhomService.findById(id)
                .map(nhom -> {
                    redirectAttributes.addFlashAttribute("editNhom", nhom);
                    redirectAttributes.addFlashAttribute("showEditModal", true);
                    return "redirect:/groups";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhóm");
                    return "redirect:/groups";
                });
    }
    
    @GetMapping("/new")
    public String newGroup(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("editNhom", new Nhom());
        redirectAttributes.addFlashAttribute("showEditModal", true);
        return "redirect:/groups";
    }
}