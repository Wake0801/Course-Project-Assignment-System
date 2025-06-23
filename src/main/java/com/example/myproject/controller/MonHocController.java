package com.example.myproject.controller;

import com.example.myproject.entity.MonHoc;
import com.example.myproject.service.MonHocService;
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
@RequestMapping("/subjects")
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    @GetMapping
    public String listSubjects(
            @RequestParam(value = "search", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "30") int size,
            Model model
    ) {
        Page<MonHoc> subjectPage = monHocService.findMonHocs(keyword, page, size);
        if (!model.containsAttribute("editSubject")) {
            model.addAttribute("editSubject", new MonHoc());
        }
        model.addAttribute("ListSubjects", subjectPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", subjectPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        return "admin/manageSubject";
    }

    @PostMapping("/save")
    public String saveSubject(@Valid @ModelAttribute("editSubject") MonHoc subject,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editSubject", result);
            redirectAttributes.addFlashAttribute("editSubject", subject);
            return "redirect:/subjects";
        }
        try {
            boolean isUpdate = monHocService.findById(subject.getMaMon()).isPresent();
            if (!isUpdate && subject.getMaMon() != null && !monHocService.findById(subject.getMaMon()).isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Mã môn học đã tồn tại!");
                redirectAttributes.addFlashAttribute("editSubject", subject);
                return "redirect:/subjects";
            }
            monHocService.save(subject);
            if (isUpdate) {
                redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
            } else {
                redirectAttributes.addFlashAttribute("message", "Thêm thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editSubject", subject);
        }
        return "redirect:/subjects";
    }

    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            monHocService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa môn học thành công");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy môn học với ID: " + id);
        }
        return "redirect:/subjects";
    }

    @GetMapping("/edit/{id}")
    public String editSubject(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        return monHocService.findById(id)
                .map(mh -> {
                    redirectAttributes.addFlashAttribute("editSubject", mh);
                    return "redirect:/subjects";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy môn học");
                    return "redirect:/subjects";
                });
    }
}
