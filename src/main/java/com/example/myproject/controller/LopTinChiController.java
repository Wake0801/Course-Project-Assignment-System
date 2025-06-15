package com.example.myproject.controller;

import com.example.myproject.entity.LopTinChi;
import com.example.myproject.entity.MonHoc;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.service.LopTinChiService;
import com.example.myproject.repository.MonHocRepository;
import com.example.myproject.repository.GiangVienRepository;
import com.example.myproject.repository.SinhVien_LTCRepository;
import com.example.myproject.repository.SinhVienRepository;
import com.example.myproject.entity.SinhVien_LTC;
import com.example.myproject.entity.SinhVien_LTCPK;
import com.example.myproject.entity.LoaiBaoCao;
import com.example.myproject.entity.LoaiBaoCao_LopTC;
import com.example.myproject.repository.LoaiBaoCaoRepository;
import com.example.myproject.repository.LoaiBaoCao_LopTCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ltc")
public class LopTinChiController {

    @Autowired
    private LopTinChiService lopTinChiService;

    @Autowired
    private MonHocRepository monHocRepository;

    @Autowired
    private GiangVienRepository giangVienRepository;

    @Autowired
    private SinhVien_LTCRepository sinhVienLTCRepository;


    @Autowired
    private SinhVienRepository sinhVienRepository;

    @Autowired
    private LoaiBaoCaoRepository loaiBaoCaoRepository;
    @Autowired
    private LoaiBaoCao_LopTCRepository loaiBaoCaoLopTCRepository;

    @GetMapping
    public String listLTC(
            @RequestParam(value = "search", required = false) String keyword,
            @RequestParam(value = "nienKhoa", required = false) String nienKhoa,
            @RequestParam(value = "hocKi", required = false) Integer hocKi,
            @RequestParam(value = "maGV", required = false) String maGV,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai,
            @RequestParam(value = "sortNgayLap", required = false, defaultValue = "desc") String sortNgayLap,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", required = false) Integer size,
            Model model
    ) {
        if (size == null) size = 10;
        Page<LopTinChi> ltcPage = lopTinChiService.findLopTinChisAdvanced(
            keyword, nienKhoa, hocKi, maGV, trangThai, page, size, sortNgayLap
        );
        if (!model.containsAttribute("editLTC")) {
            model.addAttribute("editLTC", new LopTinChi());
        }
        List<MonHoc> listMonHoc = monHocRepository.findAll();
        List<GiangVien> listGiangVien = giangVienRepository.findAll();
        // Lấy danh sách niên khóa duy nhất
        List<String> listNienKhoa = lopTinChiService.findAllNienKhoa();
        model.addAttribute("ListLTC", ltcPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ltcPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        model.addAttribute("listMonHoc", listMonHoc);
        model.addAttribute("listGiangVien", listGiangVien);
        model.addAttribute("listNienKhoa", listNienKhoa);
        model.addAttribute("filterNienKhoa", nienKhoa);
        model.addAttribute("filterHocKi", hocKi);
        model.addAttribute("filterMaGV", maGV);
        model.addAttribute("filterTrangThai", trangThai);
        model.addAttribute("sortNgayLap", sortNgayLap);

        List<LoaiBaoCao> listLoaiBaoCao = loaiBaoCaoRepository.findAll();
        model.addAttribute("listLoaiBaoCao", listLoaiBaoCao);

        // Nếu đang sửa, truyền map hệ số điểm ra view
        Map<String, java.math.BigDecimal> editHeSoDiemMap = new java.util.HashMap<>();
        LopTinChi editLTC = (LopTinChi) model.getAttribute("editLTC");
        if (editLTC != null && editLTC.getMaLopTC() != null) {
            var heSoList = loaiBaoCaoLopTCRepository.findAllByMaLopTC(editLTC.getMaLopTC());
            for (var h : heSoList) {
                editHeSoDiemMap.put(String.valueOf(h.getMaLoaiBaoCao()), h.getHeSoDiem());
            }
        }
        model.addAttribute("editHeSoDiemMap", editHeSoDiemMap);

        return "admin/manageLTC";
    }

    @PostMapping("/save")
    public String saveLTC(
            @ModelAttribute("editLTC") LopTinChi ltc,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            @RequestParam(value = "trangThai", required = false) String trangThaiStr,
            @RequestParam Map<String, String> allParams // lấy toàn bộ param để lấy hệ số điểm động
    ) {
        // Đảm bảo luôn set số lượng tối thiểu là 10 trước khi validate binding
        ltc.setSoLuongToiThieu(10);

        // Không dùng @Valid vì không validate được khi số lượng tối thiểu là rỗng (do readonly input)
        // Tự kiểm tra các trường bắt buộc và cảnh báo hợp lý
        if (ltc.getMaLopTC() == null || ltc.getMaLopTC().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mã lớp tín chỉ không được để trống!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        if (ltc.getMaMon() == null || ltc.getMaMon().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn mã môn học!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        if (ltc.getMaGV() == null || ltc.getMaGV().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn mã giảng viên!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        if (ltc.getSoLuongToiDa() == null || ltc.getSoLuongToiDa() < 1) {
            redirectAttributes.addFlashAttribute("error", "Số lượng tối đa phải lớn hơn 0!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        if (ltc.getHocKi() < 1 || ltc.getHocKi() > 3) {
            redirectAttributes.addFlashAttribute("error", "Học kỳ chỉ được phép là 1, 2 hoặc 3!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        if (ltc.getNienKhoa() == null || !ltc.getNienKhoa().matches("\\d{4}-\\d{4}")) {
            redirectAttributes.addFlashAttribute("error", "Niên khóa phải đúng định dạng (vd: 2024-2025)!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        String[] years = ltc.getNienKhoa().split("-");
        int y1 = Integer.parseInt(years[0]);
        int y2 = Integer.parseInt(years[1]);
        if (y2 != y1 + 1) {
            redirectAttributes.addFlashAttribute("error", "Niên khóa không hợp lệ: năm sau phải lớn hơn năm trước đúng 1!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        if (ltc.getSoLuongToiDa() < ltc.getSoLuongToiThieu()) {
            redirectAttributes.addFlashAttribute("error", "Số lượng tối đa phải lớn hơn hoặc bằng số lượng tối thiểu!");
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/ltc";
        }
        try {
            boolean isUpdate = lopTinChiService.findById(ltc.getMaLopTC()).isPresent();
            if (!isUpdate && ltc.getMaLopTC() != null && lopTinChiService.findById(ltc.getMaLopTC()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Mã lớp tín chỉ đã tồn tại!");
                redirectAttributes.addFlashAttribute("editLTC", ltc);
                redirectAttributes.addFlashAttribute("showModal", true);
                return "redirect:/ltc";
            }
            // Đảm bảo luôn set ngày lập nếu bị null (cho cả thêm mới và cập nhật)
            if (ltc.getNgayLap() == null) {
                ltc.setNgayLap(java.time.LocalDateTime.now());
            }
            if (!isUpdate) {
                ltc.setTrangThai(true);
            }
            // Xử lý trạng thái (nếu có)
            if (trangThaiStr != null) {
                ltc.setTrangThai("true".equals(trangThaiStr));
            }
            // Lưu lớp tín chỉ như cũ
            lopTinChiService.save(ltc);

            // Xử lý hệ số điểm các loại báo cáo
            List<LoaiBaoCao> listLoaiBaoCao = loaiBaoCaoRepository.findAll();
            for (LoaiBaoCao lbc : listLoaiBaoCao) {
                String key = "heSoDiem_" + lbc.getMaLoaiBaoCao();
                if (allParams.containsKey(key)) {
                    try {
                        java.math.BigDecimal heSo = new java.math.BigDecimal(allParams.get(key));
                        // Sửa dòng này: ép kiểu String -> int
                        loaiBaoCaoLopTCRepository.deleteByMaLopTCAndMaLoaiBaoCao(
                            ltc.getMaLopTC(),
                            Integer.parseInt(lbc.getMaLoaiBaoCao())
                        );
                        // Lưu mới
                        var entity = new LoaiBaoCao_LopTC();
                        entity.setMaLopTC(ltc.getMaLopTC());
                        entity.setMaLoaiBaoCao(Integer.parseInt(lbc.getMaLoaiBaoCao()));
                        entity.setHeSoDiem(heSo);
                        loaiBaoCaoLopTCRepository.save(entity);
                    } catch (Exception ignore) {}
                }
            }
            if (isUpdate) {
                redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
            } else {
                redirectAttributes.addFlashAttribute("message", "Thêm thành công!");
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("constraint") && msg.contains("MaGV")) {
                redirectAttributes.addFlashAttribute("error", "Mã giảng viên không tồn tại!");
            } else if (msg != null && msg.contains("constraint") && msg.contains("MaMon")) {
                redirectAttributes.addFlashAttribute("error", "Mã môn học không tồn tại!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Lỗi: " + msg);
            }
            redirectAttributes.addFlashAttribute("editLTC", ltc);
            redirectAttributes.addFlashAttribute("showModal", true);
        }
        return "redirect:/ltc";
    }

    @GetMapping("/delete/{id}")
    public String deleteLTC(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        // Kiểm tra lớp còn sinh viên không
        List<SinhVien> students = sinhVienLTCRepository.findSinhViensByMaLopTC(id);
        if (students != null && !students.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa lớp tín chỉ vì vẫn còn sinh viên trong lớp.");
            return "redirect:/ltc";
        }
        try {
            lopTinChiService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa lớp tín chỉ thành công");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy lớp tín chỉ với ID: " + id);
        }
        return "redirect:/ltc";
    }

    @GetMapping("/edit/{id}")
    public String editLTC(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        return lopTinChiService.findById(id)
                .map(ltc -> {
                    redirectAttributes.addFlashAttribute("editLTC", ltc);
                    return "redirect:/ltc";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy lớp tín chỉ");
                    return "redirect:/ltc";
                });
    }

    // API trả về danh sách sinh viên của lớp tín chỉ (JSON)
    @GetMapping("/{maLTC}/students")
    @ResponseBody
    public ResponseEntity<?> getStudentsOfLTC(@PathVariable("maLTC") String maLTC) {
        // Lấy danh sách sinh viên theo mã lớp tín chỉ qua repository
        List<SinhVien> students = sinhVienLTCRepository.findSinhViensByMaLopTC(maLTC);

        // Chuyển đổi sang map để trả về JSON (nếu cần tuỳ chỉnh trường)
        List<java.util.Map<String, Object>> result = students.stream().map(sv -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("maSV", sv.getMaSV());
            map.put("ho", sv.getHo());
            map.put("ten", sv.getTen());
            map.put("gioiTinh", sv.isGioiTinh());
            map.put("ngaySinh", sv.getNgaySinh());
            map.put("email", sv.getEmail());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // API trả về thông tin lớp tín chỉ (dùng cho sửa)
    @GetMapping("/{maLTC}/info")
    @ResponseBody
    public ResponseEntity<?> getLTCInfo(@PathVariable("maLTC") String maLTC) {
        return lopTinChiService.findById(maLTC)
                .map(ltc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maLopTC", ltc.getMaLopTC());
                    map.put("maMon", ltc.getMaMon());
                    map.put("maGV", ltc.getMaGV());
                    map.put("soLuongToiDa", ltc.getSoLuongToiDa());
                    map.put("soLuongToiThieu", ltc.getSoLuongToiThieu());
                    map.put("hocKi", ltc.getHocKi());
                    map.put("nienKhoa", ltc.getNienKhoa());
                    map.put("trangThai", ltc.isTrangThai());
                    map.put("ngayLap", ltc.getNgayLap() != null ? ltc.getNgayLap().toString() : "");
                    return ResponseEntity.ok(map);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Sinh mã lớp tín chỉ ngẫu nhiên, không trùng lặp
    @GetMapping("/next-maLopTC")
    @ResponseBody
    public String getNextMaLopTC() {
        Random random = new Random();
        String ma;
        int tries = 0;
        do {
            int num = random.nextInt(900) + 100; // 100-999
            ma = String.format("LTC%03d", num);
            tries++;
            // Tránh vòng lặp vô hạn nếu quá nhiều mã đã tồn tại
            if (tries > 1000) break;
        } while (lopTinChiService.findById(ma).isPresent());
        return ma;
    }

    // Thêm sinh viên vào lớp tín chỉ
    @PostMapping("/{maLTC}/students")
    @ResponseBody
    public Map<String, Object> addStudentToLTC(@PathVariable("maLTC") String maLTC, @RequestBody Map<String, String> payload) {
        Map<String, Object> resp = new HashMap<>();
        String maSV = payload.get("maSV");
        if (maSV == null || maSV.trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "Mã sinh viên không hợp lệ");
            return resp;
        }
        var ltcOpt = lopTinChiService.findById(maLTC);
        var svOpt = sinhVienRepository.findById(maSV);
        if (ltcOpt.isEmpty() || svOpt.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "Không tìm thấy lớp tín chỉ hoặc sinh viên");
            return resp;
        }
        // Kiểm tra đã tồn tại chưa
        SinhVien_LTCPK pk = new SinhVien_LTCPK(maSV, maLTC);
        if (sinhVienLTCRepository.existsById(pk)) {
            resp.put("success", false);
            resp.put("message", "Sinh viên đã có trong lớp này");
            return resp;
        }
        // Kiểm tra số lượng tối đa
        int currentCount = sinhVienLTCRepository.findSinhViensByMaLopTC(maLTC).size();
        int maxCount = ltcOpt.get().getSoLuongToiDa();
        if (currentCount >= maxCount) {
            resp.put("success", false);
            resp.put("message", "Lớp tín chỉ đã đủ số lượng sinh viên tối đa");
            return resp;
        }
        SinhVien_LTC svltc = new SinhVien_LTC();
        svltc.setSinhVien(svOpt.get());
        svltc.setLopTinChi(ltcOpt.get());
        sinhVienLTCRepository.save(svltc);
        resp.put("success", true);
        return resp;
    }

    // Xóa sinh viên khỏi lớp tín chỉ
    @DeleteMapping("/{maLTC}/students/{maSV}")
    @ResponseBody
    public Map<String, Object> removeStudentFromLTC(@PathVariable("maLTC") String maLTC, @PathVariable("maSV") String maSV) {
        Map<String, Object> resp = new HashMap<>();
        SinhVien_LTCPK pk = new SinhVien_LTCPK(maSV, maLTC);
        if (!sinhVienLTCRepository.existsById(pk)) {
            resp.put("success", false);
            resp.put("message", "Không tìm thấy sinh viên trong lớp này");
            return resp;
        }
        sinhVienLTCRepository.deleteById(pk);
        resp.put("success", true);
        return resp;
    }

    // API trả về danh sách hệ số điểm các loại báo cáo cho một lớp tín chỉ (dùng cho chi tiết)
    @GetMapping("/{maLTC}/hesodiem")
    @ResponseBody
    public List<Map<String, Object>> getHeSoDiemLopTC(@PathVariable("maLTC") String maLTC) {
        List<LoaiBaoCao_LopTC> list = loaiBaoCaoLopTCRepository.findAllByMaLopTC(maLTC);
        List<LoaiBaoCao> loaiBaoCaoList = loaiBaoCaoRepository.findAll();
        Map<Integer, String> tenLoaiBaoCaoMap = loaiBaoCaoList.stream()
            .collect(Collectors.toMap(
                lbc -> Integer.parseInt(lbc.getMaLoaiBaoCao()),
                LoaiBaoCao::getTenLoaiBaoCao
            ));
        return list.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("maLoaiBaoCao", item.getMaLoaiBaoCao());
            map.put("tenLoaiBaoCao", tenLoaiBaoCaoMap.get(item.getMaLoaiBaoCao()));
            map.put("heSoDiem", item.getHeSoDiem());
            return map;
        }).collect(Collectors.toList());
    }
}
