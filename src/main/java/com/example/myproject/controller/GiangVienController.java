package com.example.myproject.controller;

import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.Khoa;
import com.example.myproject.service.GiangVienService;
import com.example.myproject.service.KhoaService;
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

@Controller
@RequestMapping("/lecturers")
public class GiangVienController {

    @Autowired
    private GiangVienService lecturerService;

    @Autowired
    private KhoaService khoaService;

    @GetMapping
    public String listLecturers(
        @RequestParam(value = "search", required = false) String keyword,
        @RequestParam(value = "maKhoa", required = false) String maKhoa,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "30") int size,
        Model model
    ) {
        // Đảm bảo page >= 1
        if (page < 1) {
            page = 1;
        }
        
        Page<GiangVien> lecturerPage = lecturerService.findGiangViensWithFilter(keyword, maKhoa, page, size);
        
        // Nếu page vượt quá totalPages và có kết quả, redirect về trang cuối
        if (lecturerPage.getTotalPages() > 0 && page > lecturerPage.getTotalPages()) {
            return "redirect:/lecturers?page=" + lecturerPage.getTotalPages() + 
                   "&size=" + size + 
                   (keyword != null ? "&search=" + keyword : "") +
                   (maKhoa != null ? "&maKhoa=" + maKhoa : "");
        }
        
        if (!model.containsAttribute("editLecturer")) {
            model.addAttribute("editLecturer", new GiangVien());
            model.addAttribute("showEditModal", false);
        }
        
        model.addAttribute("ListLecturers", lecturerPage.getContent());
        // Hiển thị currentPage = 1 nếu không có kết quả, ngược lại hiển thị page thực tế
        model.addAttribute("currentPage", lecturerPage.getTotalPages() > 0 ? page : 1);
        model.addAttribute("totalPages", lecturerPage.getTotalPages());
        model.addAttribute("totalElements", lecturerPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedKhoa", maKhoa);
        model.addAttribute("size", size);
        model.addAttribute("listKhoa", khoaService.getAllKhoa());
        
        return "admin/manageLecturer";
    }

    @PostMapping("/save")
    public String saveLecturer(@Valid @ModelAttribute("editLecturer") GiangVien lecturer,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editLecturer", result);
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/lecturers";
        }
        
        try {
            boolean isEdit = lecturer.getMaGV() != null && !lecturer.getMaGV().isEmpty();
            lecturerService.save(lecturer);
            
            if (isEdit) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật giảng viên thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Thêm giảng viên thành công!");
            }
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Mã GV hoặc Mã TK đã tồn tại hoặc Mã Khoa không hợp lệ.");
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        } 
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        
        return "redirect:/lecturers";
    }

    @GetMapping("/edit/{maGV}")
    public String editLecturer(@PathVariable("maGV") String maGV, 
                             RedirectAttributes redirectAttributes) {
        try {
            GiangVien lecturer = lecturerService.findById(maGV)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giảng viên với mã: " + maGV));
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải thông tin giảng viên: " + e.getMessage());
        }
        return "redirect:/lecturers";
    }

    @GetMapping("/delete/{maGV}")
    public String deleteLecturer(@PathVariable("maGV") String maGV,
                               RedirectAttributes redirectAttributes) {
        try {
            lecturerService.deleteById(maGV);
            redirectAttributes.addFlashAttribute("success", "Xóa giảng viên thành công!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giảng viên để xóa!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa giảng viên này vì đang được sử dụng trong hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xóa giảng viên: " + e.getMessage());
        }
        return "redirect:/lecturers";
    }

    @GetMapping("/new")
    public String newLecturer(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("editLecturer", new GiangVien());
        redirectAttributes.addFlashAttribute("showEditModal", true);
        return "redirect:/lecturers";
    }
}