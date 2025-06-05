package com.example.myproject.controller;

import com.example.myproject.entity.SinhVien;
import com.example.myproject.service.SinhVienService;
import com.example.myproject.service.KhoaService;
import com.example.myproject.service.LopService;

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
import org.springframework.dao.DataIntegrityViolationException;

@Controller
@RequestMapping("/students")
public class SinhVienController {

    @Autowired
    private SinhVienService studentService;
    
    @Autowired
    private KhoaService khoaService;
    
    @Autowired
    private LopService lopService;

    @GetMapping
    public String listStudents(
        @RequestParam(value = "search", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "30") int size,
        Model model
    ) {
        // Đảm bảo page >= 1
        if (page < 1) {
            page = 1;
        }
        
        Page<SinhVien> studentPage = studentService.findSinhViens(keyword, page, size);
        
        // Nếu page vượt quá totalPages và có kết quả, redirect về trang cuối
        if (studentPage.getTotalPages() > 0 && page > studentPage.getTotalPages()) {
            return "redirect:/students?page=" + studentPage.getTotalPages() + 
                   "&size=" + size + 
                   (keyword != null ? "&search=" + keyword : "");
        }
        
        if (!model.containsAttribute("editStudent")) {
            model.addAttribute("editStudent", new SinhVien());
            model.addAttribute("showEditModal", false);
        }
        
        model.addAttribute("ListStudents", studentPage.getContent());
        // Hiển thị currentPage = 1 nếu không có kết quả, ngược lại hiển thị page thực tế
        model.addAttribute("currentPage", studentPage.getTotalPages() > 0 ? page : 1);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        
        return "admin/manageStudent";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute("editStudent") SinhVien student,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        
        // Chuyển mã SV thành uppercase
        if (student.getMaSV() != null) {
            student.setMaSV(student.getMaSV().toUpperCase());
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editStudent", result);
            redirectAttributes.addFlashAttribute("editStudent", student);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/students";
        }
        
        try {
            boolean isEdit = student.getMaSV() != null && !student.getMaSV().isEmpty() && studentService.findById(student.getMaSV()).isPresent();
            studentService.save(student);
            
            if (isEdit) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật sinh viên thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Thêm sinh viên thành công!");
            }
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Lỗi: ";
            if (e.getMessage().contains("Email")) {
                errorMessage += "Email đã tồn tại hoặc không được để trống.";
            } else if (e.getMessage().contains("MaSV")) {
                errorMessage += "Mã SV đã tồn tại.";
            } else if (e.getMessage().contains("MaTK")) {
                errorMessage += "Mã TK đã tồn tại hoặc không hợp lệ.";
            } else if (e.getMessage().contains("MaLop")) {
                errorMessage += "Mã Lớp không hợp lệ.";
            } else {
                errorMessage += "Dữ liệu bị trùng lặp hoặc không hợp lệ.";
            }
            redirectAttributes.addFlashAttribute("error", errorMessage);
            redirectAttributes.addFlashAttribute("editStudent", student);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        } 
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editStudent", student);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editStudent", student);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sinh viên thành công!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sinh viên với mã: " + id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sinh viên này vì đang được sử dụng trong hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xóa sinh viên: " + e.getMessage());
        }
        return "redirect:/students";
    }
    
    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        return studentService.findById(id)
                .map(sv -> {
                    redirectAttributes.addFlashAttribute("editStudent", sv);
                    redirectAttributes.addFlashAttribute("showEditModal", true);
                    // Bỏ thông báo success không cần thiết khi mở form sửa
                    return "redirect:/students";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy sinh viên");
                    return "redirect:/students";
                });
    }
    
    @GetMapping("/new")
    public String newStudent(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("editStudent", new SinhVien());
        redirectAttributes.addFlashAttribute("showEditModal", true);
        return "redirect:/students";
    }
}
