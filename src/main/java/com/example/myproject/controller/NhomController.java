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
        return "admin/manageGroup";
    }

    @GetMapping("/next-maNhom")
    @ResponseBody
    public Integer nextMaNhom() {
        return nhomService.getNextMaNhom();
    }

    @GetMapping("/{maNhom}/info")
    @ResponseBody
    public ResponseEntity<Nhom> nhomInfo(@PathVariable Integer maNhom) {
        Nhom nhom = nhomService.getNhom(maNhom);
        if (nhom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nhom);
    }

    @PostMapping("/save")
    public String saveNhom(@ModelAttribute Nhom nhom, Model model) {
        if (nhom.getMaNhom() == null) {
            nhom.setNgayLapNhom(new java.util.Date());
            nhomService.saveNhom(nhom);
        } else {
            Nhom old = nhomService.getNhom(nhom.getMaNhom());
            if (old != null) {
                old.setTenNhom(nhom.getTenNhom());
                old.setSoLuongTVToiDa(nhom.getSoLuongTVToiDa());
                old.setNgayDongDangKyNhom(nhom.getNgayDongDangKyNhom());
                // Đảm bảo giữ lại mã lớp TC cũ nếu không truyền lên (trường hợp input bị thiếu)
                if (nhom.getMaLopTC() != null && !nhom.getMaLopTC().isEmpty()) {
                    old.setMaLopTC(nhom.getMaLopTC());
                }
                nhomService.saveNhom(old);
            }
        }
        model.addAttribute("message", "Lưu nhóm thành công!");
        return "redirect:/group";
    }

    @GetMapping("/edit/{maNhom}")
    public String editNhom(@PathVariable Integer maNhom, Model model) {
        Nhom nhom = nhomService.getNhom(maNhom);
        List<LopTinChi> listLopTC = lopTinChiService.getAll();
        String maLopTC = nhom != null ? nhom.getMaLopTC() : null;
        List<Nhom> list = nhomService.getAllNhom(maLopTC);
        model.addAttribute("listNhom", list != null ? list : new ArrayList<>());
        model.addAttribute("listLopTC", listLopTC);
        model.addAttribute("keyword", maLopTC != null ? maLopTC : "");
        model.addAttribute("totalPages", 1);
        model.addAttribute("currentPage", 1);
        model.addAttribute("size", 20);
        model.addAttribute("editNhom", nhom);
        model.addAttribute("showModal", true);
        return "admin/manageGroup";
    }

    @GetMapping("/delete/{maNhom}")
    public String deleteNhom(@PathVariable Integer maNhom, Model model) {
        nhomService.deleteNhom(maNhom);
        model.addAttribute("message", "Xóa nhóm thành công!");
        return "redirect:/group";
    }

    @GetMapping("/{maNhom}/students")
    @ResponseBody
    public List<Map<String, Object>> getSinhVienNhom(@PathVariable Integer maNhom) {
        return nhomService.getSinhVienInNhom(maNhom);
    }

    @PostMapping("/{maNhom}/students")
    @ResponseBody
    public Map<String, Object> addSinhVienNhom(@PathVariable Integer maNhom, @RequestBody Map<String, String> body) {
        String maSV = body.get("maSV");
        Map<String, Object> resp = new HashMap<>();
        Nhom nhom = nhomService.getNhom(maNhom);
        if (nhom == null) {
            resp.put("success", false);
            resp.put("message", "Không tìm thấy nhóm!");
            return resp;
        }
        // Kiểm tra sinh viên có thuộc LTC không
        boolean svInLTC = nhomService.isSinhVienInLTC(nhom.getMaLopTC(), maSV);
        if (!svInLTC) {
            resp.put("success", false);
            resp.put("message", "Sinh viên không thuộc lớp tín chỉ này!");
            return resp;
        }
        boolean ok = nhomService.addSinhVienToNhom(maNhom, maSV);
        resp.put("success", ok);
        resp.put("message", ok ? "Thêm thành viên thành công!" : "Không thể thêm thành viên!");
        return resp;
    }

    @DeleteMapping("/{maNhom}/students/{maSV}")
    @ResponseBody
    public Map<String, Object> removeSinhVienNhom(@PathVariable Integer maNhom, @PathVariable String maSV) {
        boolean ok = nhomService.removeSinhVienFromNhom(maNhom, maSV);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", ok);
        resp.put("message", ok ? "Xóa thành viên thành công!" : "Không thể xóa thành viên!");
        return resp;
    }
}
