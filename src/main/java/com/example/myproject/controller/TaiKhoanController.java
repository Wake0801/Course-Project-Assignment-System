package com.example.myproject.controller;

import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.entity.Quyen;
import com.example.myproject.service.TaiKhoanService;
import com.example.myproject.repository.QuyenRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/accounts")
public class TaiKhoanController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private QuyenRepository quyenRepository;

    @GetMapping
    public String listAccounts(Model model,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TaiKhoan> accountPage = taiKhoanService.listAll(pageable, searchKeyword);

        model.addAttribute("ListAccounts", accountPage.getContent());
        model.addAttribute("userMaps", taiKhoanService.getUserDetailsMap(accountPage));
        model.addAttribute("currentPage", accountPage.getNumber() + 1);
        model.addAttribute("totalPages", accountPage.getTotalPages());
        model.addAttribute("totalItems", accountPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("searchKeyword", searchKeyword);
        
        if (!model.containsAttribute("newAccount")) {
            TaiKhoan newAccount = new TaiKhoan();
            newAccount.setQuyen(new Quyen());
            model.addAttribute("newAccount", newAccount);
        }
        model.addAttribute("ListQuyen", quyenRepository.findAll());
        
        // Kiểm tra nếu có attribute showEditModal và thêm vào model
        boolean showEditModal = model.getAttribute("editModalOpen") != null && (boolean) model.getAttribute("editModalOpen")
                || model.getAttribute("errorModalOpen") != null && (boolean) model.getAttribute("errorModalOpen");
        model.addAttribute("showEditModal", showEditModal);

        return "admin/manageAccounts";
    }
    
    @GetMapping("/new")
    public String newAccount(Model model) {
        TaiKhoan newAccount = new TaiKhoan();
        newAccount.setQuyen(new Quyen());
        model.addAttribute("newAccount", newAccount);
        model.addAttribute("ListQuyen", quyenRepository.findAll());
        model.addAttribute("showEditModal", true);
        return "admin/manageAccounts";
    }
    
    @GetMapping("/edit/{maTK}")
    public String editAccount(@PathVariable String maTK, Model model) {
        Optional<TaiKhoan> taiKhoan = taiKhoanService.getTaiKhoanById(maTK);
        
        if (taiKhoan.isPresent()) {
            model.addAttribute("newAccount", taiKhoan.get());
            model.addAttribute("ListQuyen", quyenRepository.findAll());
            model.addAttribute("showEditModal", true);
            
            // Lấy danh sách tài khoản cho bảng
            Pageable pageable = PageRequest.of(0, 10);
            Page<TaiKhoan> accountPage = taiKhoanService.listAll(pageable, null);
            model.addAttribute("ListAccounts", accountPage.getContent());
            model.addAttribute("userMaps", taiKhoanService.getUserDetailsMap(accountPage));
            model.addAttribute("currentPage", accountPage.getNumber() + 1);
            model.addAttribute("totalPages", accountPage.getTotalPages());
            model.addAttribute("size", 10);
            
            return "admin/manageAccounts";
        }
        
        return "redirect:/accounts";
    }

    @PostMapping("/save")
    public String saveAccount(@Valid @ModelAttribute("newAccount") TaiKhoan taiKhoan,
                              @RequestParam(name = "password", required = false) String rawPassword,
                              @RequestParam("quyen.maQuyen") String maQuyen,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        Optional<Quyen> quyenOpt = quyenRepository.findById(maQuyen);
        if (!quyenOpt.isPresent()) {
            bindingResult.rejectValue("quyen", "error.newAccount", "Quyền không hợp lệ.");
        } else {
            taiKhoan.setQuyen(quyenOpt.get());
        }

        // Kiểm tra maTK đã tồn tại khi thêm mới
        boolean isEditMode = taiKhoan.getMaTK() != null && !taiKhoan.getMaTK().isEmpty() && taiKhoanService.existsById(taiKhoan.getMaTK());
        if (!isEditMode && taiKhoanService.existsById(taiKhoan.getMaTK())) {
            bindingResult.rejectValue("maTK", "error.newAccount", "Mã tài khoản đã tồn tại.");
        }
         if (taiKhoan.getUsername() == null || taiKhoan.getUsername().trim().isEmpty()) {
             bindingResult.rejectValue("username", "error.newAccount", "Tên đăng nhập không được trống.");
        }
        if (taiKhoan.getLoaiTK() == null || taiKhoan.getLoaiTK().trim().isEmpty()) {
             bindingResult.rejectValue("loaiTK", "error.newAccount", "Loại tài khoản không được trống.");
        }

        // Mật khẩu là bắt buộc khi thêm mới
        if (!isEditMode && (rawPassword == null || rawPassword.trim().isEmpty())) {
            bindingResult.rejectValue("password", "error.newAccount", "Mật khẩu không được để trống khi tạo mới.");
        }

        if (bindingResult.hasErrors()) {
            Pageable pageable = PageRequest.of(0, 10);
            Page<TaiKhoan> accountPage = taiKhoanService.listAll(pageable, null);
            model.addAttribute("ListAccounts", accountPage.getContent());
            model.addAttribute("userMaps", taiKhoanService.getUserDetailsMap(accountPage));
            model.addAttribute("currentPage", accountPage.getNumber() + 1);
            model.addAttribute("totalPages", accountPage.getTotalPages());
            model.addAttribute("size", 10);
            model.addAttribute("ListQuyen", quyenRepository.findAll());
            model.addAttribute("org.springframework.validation.BindingResult.newAccount", bindingResult);
            model.addAttribute("newAccount", taiKhoan); 
            model.addAttribute("showEditModal", true);
            return "admin/manageAccounts";
        }

        try {
            taiKhoanService.saveTaiKhoan(taiKhoan, rawPassword);
            redirectAttributes.addFlashAttribute("successMessage", (isEditMode ? "Cập nhật" : "Thêm mới") + " tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("newAccount", taiKhoan);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newAccount", bindingResult);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        return "redirect:/accounts";
    }

    @GetMapping("/delete/{maTK}")
    public String deleteAccount(@PathVariable String maTK, RedirectAttributes redirectAttributes) {
        try {
            taiKhoanService.deleteTaiKhoanById(maTK);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tài khoản thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa tài khoản: " + e.getMessage());
        }
        return "redirect:/accounts";
    }
} 