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
        
        // Thêm dữ liệu cho form cấp tài khoản
        model.addAttribute("listQuyen", quyenRepository.findAll());
        model.addAttribute("sinhViens", taiKhoanService.getSinhVienKhongCoTaiKhoan());
        model.addAttribute("giangViens", taiKhoanService.getGiangVienKhongCoTaiKhoan());
        model.addAttribute("nhanViens", taiKhoanService.getNhanVienKhongCoTaiKhoan());

        return "admin/manageAccounts";
    }

    
    @GetMapping("/delete/{maTK}")
    public String deleteAccount(@PathVariable Integer maTK, RedirectAttributes redirectAttributes) {
        try {
            taiKhoanService.deleteTaiKhoanById(maTK);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tài khoản thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa tài khoản: " + e.getMessage());
        }
        return "redirect:/accounts";
    }
    
    @GetMapping("/edit/{maTK}")
    @ResponseBody
    public TaiKhoan getAccountForEdit(@PathVariable Integer maTK) {
        Optional<TaiKhoan> taiKhoan = taiKhoanService.getTaiKhoanById(maTK);
        if (taiKhoan.isPresent()) {
            // Không trả về password vì lý do bảo mật
            TaiKhoan account = taiKhoan.get();
            TaiKhoan safeAccount = new TaiKhoan();
            safeAccount.setMaTK(account.getMaTK());
            safeAccount.setUsername(account.getUsername());
            safeAccount.setLoaiTK(account.getLoaiTK());
            safeAccount.setQuyen(account.getQuyen());
            return safeAccount;
        }
        throw new RuntimeException("Tài khoản không tồn tại");
    }
    
    @PostMapping("/save")
    public String saveAccount(@ModelAttribute TaiKhoan taiKhoan,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             RedirectAttributes redirectAttributes) {
        try {
            // Validate dữ liệu đầu vào
            if (taiKhoan.getUsername() == null || taiKhoan.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên đăng nhập không được để trống");
            }
            
            if (taiKhoan.getQuyen() == null || taiKhoan.getQuyen().getMaQuyen() == null) {
                throw new IllegalArgumentException("Quyền không được để trống");
            }
            
            if (taiKhoan.getLoaiTK() == null || taiKhoan.getLoaiTK().trim().isEmpty()) {
                throw new IllegalArgumentException("Loại tài khoản không được để trống");
            }
            
            // Kiểm tra trùng lặp username (trừ chính tài khoản đang sửa)
            Optional<TaiKhoan> existing = taiKhoanService.getTaiKhoanByUsername(taiKhoan.getUsername());
            if (existing.isPresent() && !existing.get().getMaTK().equals(taiKhoan.getMaTK())) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
            }
            
            // Lưu tài khoản với logic mật khẩu
            taiKhoanService.saveTaiKhoan(taiKhoan, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật tài khoản: " + e.getMessage());
        }
        return "redirect:/accounts";
    }
    
    @PostMapping("/captaikhoan")
    public String capTaiKhoan(@RequestParam String userRole,
                              @RequestParam String selectedUserId,
                              @RequestParam String selectedUserName,
                              @RequestParam String selectedUserEmail,
                              @RequestParam String quyenId,
                              RedirectAttributes redirectAttributes) {
        try {
            // Log input parameters với nhiều detail hơn
            System.out.println("=== CẤP TÀI KHOẢN REQUEST ===");
            System.out.println("userRole: [" + userRole + "]");
            System.out.println("selectedUserId: [" + selectedUserId + "] (length: " + (selectedUserId != null ? selectedUserId.length() : "null") + ")");
            System.out.println("selectedUserName: [" + selectedUserName + "]");
            System.out.println("selectedUserEmail: [" + selectedUserEmail + "]");
            System.out.println("quyenId (from form): [" + quyenId + "]");
            
            // Validate input
            if (selectedUserId == null || selectedUserId.trim().isEmpty()) {
                throw new RuntimeException("Vui lòng chọn người dùng");
            }
            
            if (selectedUserName == null || selectedUserName.trim().isEmpty()) {
                throw new RuntimeException("Tên người dùng không được để trống");
            }
            
            // Convert quyenId to Integer based on userRole
            Integer quyenIdInt;
            switch (userRole.toUpperCase()) {
                case "SINHVIEN":
                    quyenIdInt = 3; // SINH_VIEN có MaQuyen = 3
                    break;
                case "GIANGVIEN":
                    quyenIdInt = 2; // GIANG_VIEN có MaQuyen = 2
                    break;
                case "NHANVIENPKT":
                    quyenIdInt = 4; // NHAN_VIEN có MaQuyen = 4
                    break;
                default:
                    throw new RuntimeException("Loại người dùng không hợp lệ: " + userRole);
            }
            
            System.out.println("quyenIdInt (converted): " + quyenIdInt);
            
            // Gọi service để cấp tài khoản
            taiKhoanService.capTaiKhoan(userRole, selectedUserId, selectedUserName, selectedUserEmail, quyenIdInt);
            redirectAttributes.addFlashAttribute("successMessage", "Cấp tài khoản thành công! Email đã được gửi đến người dùng.");
            System.out.println("=== CẤP TÀI KHOẢN THÀNH CÔNG ===");
        } catch (Exception e) {
            System.err.println("=== LỖI CẤP TÀI KHOẢN ===");
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cấp tài khoản: " + e.getMessage());
        }
        return "redirect:/accounts";
    }
} 