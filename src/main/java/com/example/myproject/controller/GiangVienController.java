package com.example.myproject.controller;

import com.example.myproject.entity.GiangVien; //
import com.example.myproject.service.GiangVienService;
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
@RequestMapping("/lecturers") // Đổi đường dẫn cơ sở thành /lecturers
public class GiangVienController {

    @Autowired
    private GiangVienService lecturerService; // Đổi tên service

    @GetMapping
    public String listLecturers( // Đổi tên phương thức
        @RequestParam(value = "search", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "30") int size,
        Model model
    ) {
        Page<GiangVien> lecturerPage = lecturerService.findGiangViens(keyword, page, size); // Gọi service tương ứng
        
        // Sử dụng tên "editLecturer" cho đối tượng form trong model
        if (!model.containsAttribute("editLecturer")) { 
            model.addAttribute("editLecturer", new GiangVien());
        }
        
        // Truyền danh sách giảng viên và thông tin phân trang tới view
        model.addAttribute("ListLecturers", lecturerPage.getContent()); // Đổi tên danh sách
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", lecturerPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        
        // Trả về tên view template mới
        return "admin/manageLecturer"; 
    }

    @PostMapping("/save")
    public String saveLecturer(@Valid @ModelAttribute("editLecturer") GiangVien lecturer, // Đổi tên object và class
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            // Thêm lỗi và đối tượng vào flash attributes để hiển thị lại form với lỗi
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editLecturer", result);
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
            return "redirect:/lecturers"; // Redirect về trang danh sách
        }
        
        try {
            lecturerService.save(lecturer); // Gọi service lưu
            redirectAttributes.addFlashAttribute("success", "Lưu giảng viên thành công!");
        } catch (DataIntegrityViolationException e) { // Bắt lỗi cụ thể hơn nếu có thể (vd: trùng mã)
             redirectAttributes.addFlashAttribute("error", "Lỗi: Mã GV hoặc Mã TK đã tồn tại hoặc Mã Khoa không hợp lệ.");
             redirectAttributes.addFlashAttribute("editLecturer", lecturer); // Giữ lại thông tin đã nhập
        } 
        catch (IllegalArgumentException e) { // Bắt lỗi từ Service (vd: Khoa, TK không tồn tại)
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
        }
        catch (Exception e) { // Bắt các lỗi chung khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editLecturer", lecturer);
        }
        
        return "redirect:/lecturers"; // Redirect về trang danh sách
    }

    @GetMapping("/delete/{id}")
    public String deleteLecturer(@PathVariable("id") String id, RedirectAttributes redirectAttributes) { // Đổi tên tham số và phương thức
        try {
            lecturerService.deleteById(id); // Gọi service xóa
            redirectAttributes.addFlashAttribute("success", "Đã xóa giảng viên thành công");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy giảng viên với ID: " + id);
        } catch (DataIntegrityViolationException e){
             redirectAttributes.addFlashAttribute("error", "Không thể xóa giảng viên này do có dữ liệu liên quan.");
        } catch (Exception e) {
             redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa giảng viên: " + e.getMessage());
        }
        return "redirect:/lecturers"; // Redirect về trang danh sách
    }
    
    @GetMapping("/edit/{id}")
    public String editLecturer(@PathVariable("id") String id, RedirectAttributes redirectAttributes) { // Đổi tên phương thức
        return lecturerService.findById(id) // Gọi service tìm theo ID
                .map(gv -> {
                    redirectAttributes.addFlashAttribute("editLecturer", gv); // Đặt tên attribute là "editLecturer"
                    return "redirect:/lecturers"; // Redirect để hiển thị modal chỉnh sửa
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy giảng viên");
                    return "redirect:/lecturers"; // Redirect về trang danh sách nếu không tìm thấy
                });
    }
}