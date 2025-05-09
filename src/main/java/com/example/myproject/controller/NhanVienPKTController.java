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
        Page<NhanVienPKT> employeePage = employeeService.findNhanViens(keyword, page, size);
        if (!model.containsAttribute("editEmployee")) {
            model.addAttribute("editEmployee", new NhanVienPKT());
        }
        model.addAttribute("ListEmployees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        return "admin/manageEmployee";
    }

    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute("editEmployee") NhanVienPKT employee,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editEmployee", result);
            redirectAttributes.addFlashAttribute("editEmployee", employee);
            return "redirect:/employees";
        }
        try {
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute("success", "Lưu nhân viên thành công!");
        } catch (DataIntegrityViolationException e) {
             redirectAttributes.addFlashAttribute("error", "Lỗi: Mã NV hoặc Mã TK đã tồn tại.");
             redirectAttributes.addFlashAttribute("editEmployee", employee);
        } 
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editEmployee", employee);
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editEmployee", employee);
        }
        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa nhân viên thành công");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên với ID: " + id);
        } catch (DataIntegrityViolationException e){
             redirectAttributes.addFlashAttribute("error", "Không thể xóa nhân viên này do có dữ liệu liên quan.");
        } catch (Exception e) {
             redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa nhân viên: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        return employeeService.findById(id)
                .map(nv -> {
                    redirectAttributes.addFlashAttribute("editEmployee", nv);
                    return "redirect:/employees";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên");
                    return "redirect:/employees";
                });
    }
}