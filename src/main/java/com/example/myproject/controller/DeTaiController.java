package com.example.myproject.controller;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.MonHoc;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.service.DeTaiService;
import com.example.myproject.service.KhoaService;
import com.example.myproject.service.GiangVienService;
import com.example.myproject.service.LopTinChiService;
import com.example.myproject.service.MonHocService;
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

import java.util.List;

@Controller
@RequestMapping("/topics")
public class DeTaiController {

    @Autowired
    private DeTaiService deTaiService;
    
    @Autowired
    private KhoaService khoaService;
    
    @Autowired
    private GiangVienService giangVienService;
    
    @Autowired
    private LopTinChiService lopTinChiService;
    
    @Autowired
    private MonHocService monHocService;

    @GetMapping
    public String listTopics(
        @RequestParam(value = "search", required = false) String keyword,
        @RequestParam(value = "maKhoa", required = false) String maKhoa,
        @RequestParam(value = "maGV", required = false) String maGV,
        @RequestParam(value = "maLopTC", required = false) String maLopTC,
        @RequestParam(value = "maMon", required = false) String maMon,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "30") int size,
        Model model
    ) {
        // Debug logging
        System.out.println("=== DEBUG SEARCH/FILTER ===");
        System.out.println("keyword: " + keyword);
        System.out.println("maKhoa: " + maKhoa);
        System.out.println("maGV: " + maGV);
        System.out.println("maLopTC: " + maLopTC);
        System.out.println("maMon: " + maMon);
        System.out.println("page: " + page);
        System.out.println("size: " + size);
        
        // Đảm bảo page >= 1
        if (page < 1) {
            page = 1;
        }
        
        Page<DeTai> topicPage = deTaiService.findDeTaisWithFilter(keyword, maKhoa, maGV, maLopTC, maMon, page, size);
        
        System.out.println("Found " + topicPage.getTotalElements() + " topics");
        System.out.println("=========================");
        
        // Nếu page vượt quá totalPages và có kết quả, redirect về trang cuối
        if (topicPage.getTotalPages() > 0 && page > topicPage.getTotalPages()) {
            return "redirect:/topics?page=" + topicPage.getTotalPages() + 
                   "&size=" + size + 
                   (keyword != null ? "&search=" + keyword : "") +
                   (maKhoa != null ? "&maKhoa=" + maKhoa : "") +
                   (maGV != null ? "&maGV=" + maGV : "") +
                   (maLopTC != null ? "&maLopTC=" + maLopTC : "") +
                   (maMon != null ? "&maMon=" + maMon : "");
        }
        
        if (!model.containsAttribute("editTopic")) {
            model.addAttribute("editTopic", new DeTai());
            model.addAttribute("showEditModal", false);
        }
        
        model.addAttribute("ListTopics", topicPage.getContent());
        // Hiển thị currentPage = 1 nếu không có kết quả, ngược lại hiển thị page thực tế
        model.addAttribute("currentPage", topicPage.getTotalPages() > 0 ? page : 1);
        model.addAttribute("totalPages", topicPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        
        // Add filter data for dropdowns
        model.addAttribute("listKhoa", khoaService.getAllKhoa());
        model.addAttribute("listGiangVien", giangVienService.getAllGiangVien());
        model.addAttribute("listLopTC", lopTinChiService.getAllLopTinChi());
        model.addAttribute("listMonHoc", monHocService.getAllMonHoc());
        
        // Add selected filter values
        model.addAttribute("selectedKhoa", maKhoa);
        model.addAttribute("selectedGiangVien", maGV);
        model.addAttribute("selectedLopTC", maLopTC);
        model.addAttribute("selectedMonHoc", maMon);
        
        // Add selected filter names for display
        if (maKhoa != null && !maKhoa.trim().isEmpty()) {
            khoaService.getAllKhoa().stream()
                .filter(k -> k.getMaKhoa().equals(maKhoa))
                .findFirst()
                .ifPresent(k -> model.addAttribute("selectedKhoaName", k.getTenKhoa()));
        }
        
        if (maGV != null && !maGV.trim().isEmpty()) {
            giangVienService.getAllGiangVien().stream()
                .filter(gv -> gv.getMaGV().equals(maGV))
                .findFirst()
                .ifPresent(gv -> model.addAttribute("selectedGiangVienName", gv.getHo() + " " + gv.getTen()));
        }
        
        if (maMon != null && !maMon.trim().isEmpty()) {
            monHocService.getAllMonHoc().stream()
                .filter(mh -> mh.getMaMon().equals(maMon))
                .findFirst()
                .ifPresent(mh -> model.addAttribute("selectedMonHocName", mh.getTenMon()));
        }
        
        if (maLopTC != null && !maLopTC.trim().isEmpty()) {
            lopTinChiService.getAllLopTinChi().stream()
                .filter(ltc -> ltc.getMaLopTC().equals(maLopTC))
                .findFirst()
                .ifPresent(ltc -> model.addAttribute("selectedLopTCName", ltc.getMaLopTC() + " - " + ltc.getMonHoc().getTenMon()));
        }
        
        return "admin/manageTopics";
    }

    @GetMapping("/edit/{maDT}")
    public String showEditTopicForm(@PathVariable int maDT, RedirectAttributes redirectAttributes) {
        try {
            Optional<DeTai> deTaiOpt = deTaiService.findById(maDT);
            if (deTaiOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("editTopic", deTaiOpt.get());
                redirectAttributes.addFlashAttribute("showEditModal", true);
                redirectAttributes.addFlashAttribute("success", "Đã tải thông tin đề tài. Vui lòng thực hiện chỉnh sửa.");
                return "redirect:/topics";
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đề tài với mã: " + maDT);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tìm đề tài: " + e.getMessage());
        }
        return "redirect:/topics";
    }

    @PostMapping("/save")
    public String saveTopic(@Valid @ModelAttribute("editTopic") DeTai topic,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editTopic", result);
            redirectAttributes.addFlashAttribute("editTopic", topic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/topics";
        }
        
        try {
            // Kiểm tra xem có phải là edit mode không (có mã đề tài và đề tài đã tồn tại)
            boolean isEdit = topic.getMaDT() != null && deTaiService.findById(topic.getMaDT()).isPresent();
            
            deTaiService.save(topic);
            
            if (isEdit) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật đề tài thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Thêm đề tài thành công! Mã đề tài: " + topic.getMaDT());
            }
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Lỗi: ";
            if (e.getMessage().contains("MaDT")) {
                errorMessage += "Mã đề tài đã tồn tại.";
            } else if (e.getMessage().contains("MaLopTC")) {
                errorMessage += "Mã lớp tín chỉ không hợp lệ.";
            } else {
                errorMessage += "Dữ liệu bị trùng lặp hoặc không hợp lệ.";
            }
            redirectAttributes.addFlashAttribute("error", errorMessage);
            redirectAttributes.addFlashAttribute("editTopic", topic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        } 
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editTopic", topic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editTopic", topic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
        }
        
        return "redirect:/topics";
    }

    @GetMapping("/delete/{maDT}")
    public String deleteTopic(@PathVariable int maDT, RedirectAttributes redirectAttributes) {
        try {
            deTaiService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa đề tài thành công!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đề tài với mã: " + id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa đề tài này vì đang được sử dụng trong hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xóa đề tài: " + e.getMessage());
        }
        return "redirect:/topics";
    }

    @GetMapping("/edit/{id}")
    public String editTopic(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        return deTaiService.findById(id)
                .map(dt -> {
                    redirectAttributes.addFlashAttribute("editTopic", dt);
                    redirectAttributes.addFlashAttribute("showEditModal", true);
                    return "redirect:/topics";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đề tài");
                    return "redirect:/topics";
                });
    }
    
    @GetMapping("/new")
    public String newTopic(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("editTopic", new DeTai());
        redirectAttributes.addFlashAttribute("showEditModal", true);
        return "redirect:/topics";
    }
    
    // API endpoint để lấy giảng viên theo khoa (cho dynamic filtering)
    @GetMapping("/api/giangvien/by-khoa/{maKhoa}")
    @ResponseBody
    public List<GiangVien> getGiangVienByKhoa(@PathVariable("maKhoa") String maKhoa) {
        return giangVienService.getGiangVienByKhoa(maKhoa);
    }
    
    // API endpoint để lấy tất cả giảng viên (khi không chọn khoa)
    @GetMapping("/api/giangvien/by-khoa/")
    @ResponseBody
    public List<GiangVien> getAllGiangVien() {
        return giangVienService.getAllGiangVien();
    }
    
    // API endpoint để lấy môn học theo khoa
    @GetMapping("/api/monhoc/by-khoa/{maKhoa}")
    @ResponseBody
    public List<MonHoc> getMonHocByKhoa(@PathVariable("maKhoa") String maKhoa) {
        return monHocService.getMonHocByKhoa(maKhoa);
    }
    
    @GetMapping("/api/monhoc/by-khoa/")
    @ResponseBody
    public List<MonHoc> getAllMonHoc() {
        return monHocService.getAllMonHoc();
    }
    
    // API endpoint để lấy lớp tín chỉ theo khoa
    @GetMapping("/api/loptinchi/by-khoa/{maKhoa}")
    @ResponseBody  
    public List<LopTinChi> getLopTinChiByKhoa(@PathVariable("maKhoa") String maKhoa) {
        return lopTinChiService.getLopTinChiByKhoa(maKhoa);
    }
    
    @GetMapping("/api/loptinchi/by-khoa/")
    @ResponseBody  
    public List<LopTinChi> getAllLopTinChi() {
        return lopTinChiService.getAllLopTinChi();
    }
}