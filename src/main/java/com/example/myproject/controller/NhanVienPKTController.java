package com.example.myproject.controller;

import com.example.myproject.entity.NhanVienPKT;
import com.example.myproject.service.NhanVienPKTService;
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
@RequestMapping("/employees")
public class NhanVienPKTController {

    @Autowired
    private NhanVienPKTService employeeService;

    @GetMapping
    public String listEmployees(
        @RequestParam(value = "search", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "30") int size,
        Model model
    ) {
        // Đảm bảo page >= 1
        if (page < 1) {
            page = 1;
        }
        
        Page<NhanVienPKT> employeePage = employeeService.findNhanViens(keyword, page, size);
        
        // Nếu page vượt quá totalPages và có kết quả, redirect về trang cuối
        if (employeePage.getTotalPages() > 0 && page > employeePage.getTotalPages()) {
            return "redirect:/employees?page=" + employeePage.getTotalPages() + 
                   "&size=" + size + 
                   (keyword != null ? "&search=" + keyword : "");
        }
        
        if (!model.containsAttribute("editEmployee")) {
            model.addAttribute("editEmployee", new NhanVienPKT());
            model.addAttribute("showEditModal", false);
        }
        
        model.addAttribute("ListEmployees", employeePage.getContent());
        // Hiển thị currentPage = 1 nếu không có kết quả, ngược lại hiển thị page thực tế
        model.addAttribute("currentPage", employeePage.getTotalPages() > 0 ? page : 1);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        
        return "admin/manageEmployee";
    }

    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute("editEmployee") NhanVienPKT employee,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        
        // Chuyển mã NV thành uppercase
        if (employee.getMaNV() != null) {
            employee.setMaNV(employee.getMaNV().toUpperCase());
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editEmployee", result);
            redirectAttributes.addFlashAttribute("editEmployee", employee);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/employees";
        }
        
        try {
            boolean isEdit = employee.getMaNV() != null && !employee.getMaNV().isEmpty() && employeeService.findById(employee.getMaNV()).isPresent();
            employeeService.save(employee);
            
            if (isEdit) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công!");
            }
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Lỗi: ";
            if (e.getMessage().contains("MaNV")) {
                errorMessage += "Mã NV đã tồn tại.";
            } else if (e.getMessage().contains("MaTK")) {
                errorMessage += "Mã TK đã tồn tại hoặc không hợp lệ.";
            } else {
                errorMessage += "Dữ liệu bị trùng lặp hoặc không hợp lệ.";
            }
            redirectAttributes.addFlashAttribute("error", errorMessage);
             redirectAttributes.addFlashAttribute("editEmployee", employee);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        } 
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editEmployee", employee);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editEmployee", employee);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        
        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa nhân viên thành công!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên với mã: " + id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa nhân viên này vì đang được sử dụng trong hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xóa nhân viên: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        return employeeService.findById(id)
                .map(nv -> {
                    redirectAttributes.addFlashAttribute("editEmployee", nv);
                    redirectAttributes.addFlashAttribute("showEditModal", true);
                    // Bỏ thông báo success không cần thiết khi mở form sửa
                    return "redirect:/employees";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên");
                    return "redirect:/employees";
                });
    }
    
    @GetMapping("/new")
    public String newEmployee(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("editEmployee", new NhanVienPKT());
        redirectAttributes.addFlashAttribute("showEditModal", true);
        return "redirect:/employees";
    }
}