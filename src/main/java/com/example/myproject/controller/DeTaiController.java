package com.example.myproject.controller;

import com.example.myproject.entity.DeTai;
import com.example.myproject.service.DeTaiService;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.MonHoc;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.service.GiangVienService;
import com.example.myproject.service.MonHocService;
import com.example.myproject.service.LopTinChiService;
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
import java.util.Optional;
import java.util.List;
import com.example.myproject.entity.Khoa;
import com.example.myproject.repository.KhoaRepository;

@Controller
@RequestMapping("/topics")
public class DeTaiController {

    @Autowired
    private DeTaiService deTaiService;

    @Autowired
    private GiangVienService giangVienService;

    @Autowired
    private MonHocService monHocService;

    @Autowired
    private LopTinChiService lopTinChiService;

    @Autowired
    private KhoaRepository khoaRepository;

    @GetMapping("")
    public String manageTopics(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "search", required = false) String keyword,
        @RequestParam(value = "maKhoa", required = false) String maKhoa,
        @RequestParam(value = "maGV", required = false) String maGV,
        @RequestParam(value = "maMon", required = false) String maMon,
        @RequestParam(value = "maLopTC", required = false) String maLopTC,
        Model model
    ) {
        Page<DeTai> deTaiPage = deTaiService.findByAdvancedFilters(page, size, keyword, maKhoa, maGV, maLopTC, maMon);
        
        if (!model.containsAttribute("editTopic")) {
            model.addAttribute("editTopic", new DeTai());
            model.addAttribute("showEditModal", false);
        }
        
        model.addAttribute("ListTopics", deTaiPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", deTaiPage.getTotalPages());
        model.addAttribute("totalItems", deTaiPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        
        // Add filter data
        model.addAttribute("listKhoa", deTaiService.getAllKhoa());
        model.addAttribute("listGiangVien", deTaiService.getAllGiangVien());
        model.addAttribute("listLopTC", deTaiService.getAllLopTinChi());
        model.addAttribute("listMonHoc", monHocService.getAllMonHoc());
        
        // Add selected filter values for template
        model.addAttribute("selectedKhoa", maKhoa);
        model.addAttribute("selectedGiangVien", maGV);
        model.addAttribute("selectedMonHoc", maMon);
        model.addAttribute("selectedLopTC", maLopTC);
        
        // Add selected names for display
        try {
            if (maKhoa != null && !maKhoa.isEmpty()) {
                Optional<Khoa> khoa = khoaRepository.findById(maKhoa);
                if (khoa.isPresent()) {
                    model.addAttribute("selectedKhoaName", khoa.get().getTenKhoa());
                }
            }
            if (maGV != null && !maGV.isEmpty()) {
                Optional<GiangVien> gvOpt = giangVienService.findById(maGV);
                if (gvOpt.isPresent()) {
                    GiangVien gv = gvOpt.get();
                    model.addAttribute("selectedGiangVienName", gv.getHo() + " " + gv.getTen());
                }
            }
            if (maLopTC != null && !maLopTC.isEmpty()) {
                LopTinChi ltc = lopTinChiService.findById(maLopTC);
                if (ltc != null) {
                    model.addAttribute("selectedLopTCName", ltc.getMaLopTC() + " - " + ltc.getMonHoc().getTenMon());
                }
            }
        } catch (Exception e) {
            // Log error but continue
            System.err.println("Error getting filter names: " + e.getMessage());
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
    public String saveTopic(@ModelAttribute @Valid DeTai editTopic, 
                           BindingResult result, 
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editTopic", result);
            redirectAttributes.addFlashAttribute("editTopic", editTopic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/topics";
        }
        
        try {
            deTaiService.save(editTopic);
            redirectAttributes.addFlashAttribute("success", "Lưu đề tài thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: Dữ liệu đề tài vi phạm ràng buộc toàn vẹn");
            redirectAttributes.addFlashAttribute("editTopic", editTopic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            return "redirect:/topics";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editTopic", editTopic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            return "redirect:/topics";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu đề tài: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editTopic", editTopic);
            redirectAttributes.addFlashAttribute("showEditModal", true);
            return "redirect:/topics";
        }
        return "redirect:/topics";
    }

    @GetMapping("/delete/{maDT}")
    public String deleteTopic(@PathVariable int maDT, RedirectAttributes redirectAttributes) {
        try {
            deTaiService.deleteById(maDT);
            redirectAttributes.addFlashAttribute("success", "Xóa đề tài thành công!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đề tài để xóa!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa đề tài vì có dữ liệu liên quan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa đề tài: " + e.getMessage());
        }
        return "redirect:/topics";
    }
    
    @GetMapping("/new")
    public String newTopic(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("editTopic", new DeTai());
        redirectAttributes.addFlashAttribute("showEditModal", true);
        return "redirect:/topics";
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