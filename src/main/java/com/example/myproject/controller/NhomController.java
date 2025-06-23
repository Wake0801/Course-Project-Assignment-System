package com.example.myproject.controller;

import com.example.myproject.entity.Nhom;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.service.NhomService;
import com.example.myproject.service.LopTinChiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/group")
public class NhomController {
    @Autowired
    private NhomService nhomService;
    @Autowired
    private LopTinChiService lopTinChiService;

    @GetMapping
    public String listNhom(@RequestParam(value = "keyword", required = false) String keyword,
                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                           @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                           Model model) {
        try {
            List<Nhom> list = nhomService.getAllNhom(keyword);
            List<LopTinChi> listLopTC = lopTinChiService.getAll();
            model.addAttribute("listNhom", list != null ? list : new ArrayList<>());
            model.addAttribute("listLopTC", listLopTC);
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
            model.addAttribute("size", size);
            model.addAttribute("editNhom", null);
            model.addAttribute("showModal", false);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách nhóm: " + e.getMessage());
        }
        return "admin/manageGroup";
    }

    @GetMapping("/next-maNhom")
    @ResponseBody
    public ResponseEntity<?> nextMaNhom() {
        try {
            Integer nextMa = nhomService.getNextMaNhom();
            return ResponseEntity.ok(nextMa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy mã nhóm tiếp theo: " + e.getMessage());
        }
    }

    @GetMapping("/{maNhom}/info")
    @ResponseBody
    public ResponseEntity<?> nhomInfo(@PathVariable Integer maNhom) {
        try {
            Nhom nhom = nhomService.getNhom(maNhom);
            if (nhom == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(nhom);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy thông tin nhóm: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public String saveNhom(@Valid @ModelAttribute Nhom nhom, 
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.nhom", result);
            redirectAttributes.addFlashAttribute("editNhom", nhom);
            redirectAttributes.addFlashAttribute("showModal", true);
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/group";
        }

        try {
            if (nhom.getMaNhom() == 0) {
                nhom.setNgayLapNhom(LocalDate.now());
                nhomService.saveNhom(nhom);
                redirectAttributes.addFlashAttribute("success", "Thêm nhóm thành công!");
            } else {
                Nhom old = nhomService.getNhom(nhom.getMaNhom());
                if (old != null) {
                    old.setTenNhom(nhom.getTenNhom());
                    old.setSoLuongTVToiDa(nhom.getSoLuongTVToiDa());
                    old.setNgayDongDangKyNhom(nhom.getNgayDongDangKyNhom());
                    if (nhom.getLopTinChi() != null) {
                        old.setLopTinChi(nhom.getLopTinChi());
                    }
                    nhomService.saveNhom(old);
                    redirectAttributes.addFlashAttribute("success", "Cập nhật nhóm thành công!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhóm để cập nhật!");
                }
            }
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Dữ liệu nhóm vi phạm ràng buộc toàn vẹn");
            redirectAttributes.addFlashAttribute("editNhom", nhom);
            redirectAttributes.addFlashAttribute("showModal", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu nhóm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editNhom", nhom);
            redirectAttributes.addFlashAttribute("showModal", true);
        }
        return "redirect:/group";
    }

    @GetMapping("/edit/{maNhom}")
    public String editNhom(@PathVariable Integer maNhom, 
                          RedirectAttributes redirectAttributes) {
        try {
            Nhom nhom = nhomService.getNhom(maNhom);
            if (nhom == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhóm với mã: " + maNhom);
                return "redirect:/group";
            }
            List<LopTinChi> listLopTC = lopTinChiService.getAll();
            String maLopTC = nhom.getLopTinChi() != null ? nhom.getLopTinChi().getMaLopTC() : null;
            List<Nhom> list = nhomService.getAllNhom(maLopTC);
            redirectAttributes.addFlashAttribute("listNhom", list != null ? list : new ArrayList<>());
            redirectAttributes.addFlashAttribute("listLopTC", listLopTC);
            redirectAttributes.addFlashAttribute("keyword", maLopTC != null ? maLopTC : "");
            redirectAttributes.addFlashAttribute("editNhom", nhom);
            redirectAttributes.addFlashAttribute("showModal", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải thông tin nhóm: " + e.getMessage());
        }
        return "redirect:/group";
    }

    @GetMapping("/delete/{maNhom}")
    public String deleteNhom(@PathVariable Integer maNhom, 
                            RedirectAttributes redirectAttributes) {
        try {
            nhomService.deleteNhom(maNhom);
            redirectAttributes.addFlashAttribute("success", "Xóa nhóm thành công!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhóm để xóa!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa nhóm vì có dữ liệu liên quan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa nhóm: " + e.getMessage());
        }
        return "redirect:/group";
    }

    @GetMapping("/{maNhom}/students")
    @ResponseBody
    public ResponseEntity<?> getSinhVienNhom(@PathVariable Integer maNhom) {
        try {
            List<Map<String, Object>> students = nhomService.getSinhVienInNhom(maNhom);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy danh sách sinh viên: " + e.getMessage());
        }
    }

    @PostMapping("/{maNhom}/students")
    @ResponseBody
    public ResponseEntity<?> addSinhVienNhom(@PathVariable Integer maNhom, 
                                           @RequestBody Map<String, String> body) {
        try {
            String maSV = body.get("maSV");
            if (maSV == null || maSV.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã sinh viên không được để trống!");
            }

            Nhom nhom = nhomService.getNhom(maNhom);
            if (nhom == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy nhóm!");
            }

            boolean svInLTC = nhomService.isSinhVienInLTC(nhom.getLopTinChi().getMaLopTC(), maSV);
            if (!svInLTC) {
                return ResponseEntity.badRequest().body("Sinh viên không thuộc lớp tín chỉ này!");
            }

            boolean ok = nhomService.addSinhVienToNhom(maNhom, maSV);
            if (ok) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thêm thành viên thành công!"
                ));
            } else {
                return ResponseEntity.badRequest().body("Không thể thêm thành viên!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi thêm sinh viên vào nhóm: " + e.getMessage());
        }
    }

    @DeleteMapping("/{maNhom}/students/{maSV}")
    @ResponseBody
    public ResponseEntity<?> removeSinhVienNhom(@PathVariable Integer maNhom, 
                                              @PathVariable String maSV) {
        try {
            boolean ok = nhomService.removeSinhVienFromNhom(maNhom, maSV);
            if (ok) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa thành viên thành công!"
                ));
            } else {
                return ResponseEntity.badRequest().body("Không thể xóa thành viên!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa sinh viên khỏi nhóm: " + e.getMessage());
        }
    }
}
