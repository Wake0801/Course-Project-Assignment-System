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
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "30") int size,
        Model model
    ) {
        Page<GiangVien> lecturerPage = lecturerService.findGiangViens(keyword, page, size);
        
        if (!model.containsAttribute("editLecturer")) {
            model.addAttribute("editLecturer", new GiangVien());
            model.addAttribute("showEditModal", false);
        }
        
        model.addAttribute("ListLecturers", lecturerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", lecturerPage.getTotalPages());
        model.addAttribute("keyword", keyword);
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
            lecturerService.save(lecturer);
            redirectAttributes.addFlashAttribute("success", "Lưu giảng viên thành công!");
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
            redirectAttributes.addFlashAttribute("success", "Đã tải thông tin giảng viên. Vui lòng thực hiện chỉnh sửa.");
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