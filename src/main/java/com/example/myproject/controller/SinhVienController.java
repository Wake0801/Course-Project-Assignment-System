package com.example.myproject.controller;

import com.example.myproject.entity.SinhVien;
import com.example.myproject.service.SinhVienService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/students")
public class SinhVienController {

    @Autowired
    private SinhVienService studentService;

    @GetMapping
    public String listStudents(
        @RequestParam(value = "search", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "30") int size,
        Model model
    ) {
        Page<SinhVien> studentPage = studentService.findSinhViens(keyword, page, size);
        if (!model.containsAttribute("editStudent")) {
            model.addAttribute("editStudent", new SinhVien());
        }
        studentPage.getContent().forEach(sv -> System.out.println(
        "SV: " + sv.getMaSV() + 
        ", Lop: " + (sv.getLop() != null ? sv.getLop().getMaLop() : "null") +
        ", TK: " + (sv.getTaiKhoan() != null ? sv.getTaiKhoan().getMaTK() : "null")
        ));
        model.addAttribute("ListStudents", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        return "admin/manageStudent"; // Thymeleaf template
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute("editStudent") SinhVien student,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editStudent", result);
            redirectAttributes.addFlashAttribute("editStudent", student);
            return "redirect:/students";
        }
        
        try {
            studentService.save(student);
            redirectAttributes.addFlashAttribute("success", "Lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editStudent", student);
        }
        
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sinh viên thành công");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sinh viên với ID: " + id);
        }
        return "redirect:/students";
    }
    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
    return studentService.findById(id)
            .map(sv -> {
                redirectAttributes.addFlashAttribute("editStudent", sv);
                return "redirect:/students";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sinh viên");
                return "redirect:/students";
            });
    }

}
