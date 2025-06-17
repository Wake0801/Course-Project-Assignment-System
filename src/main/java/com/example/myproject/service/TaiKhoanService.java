package com.example.myproject.service;

import com.example.myproject.entity.TaiKhoan;
import com.example.myproject.entity.Quyen;
import com.example.myproject.entity.SinhVien;
import com.example.myproject.entity.GiangVien;
import com.example.myproject.entity.NhanVienPKT;
import com.example.myproject.repository.TaiKhoanRepository;
import com.example.myproject.repository.QuyenRepository;
import com.example.myproject.repository.SinhVienRepository;
import com.example.myproject.repository.GiangVienRepository;
import com.example.myproject.repository.NhanVienPKTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private QuyenRepository quyenRepository;

    @Autowired
    private SinhVienRepository sinhVienRepository;

    @Autowired
    private GiangVienRepository giangVienRepository;

    @Autowired
    private NhanVienPKTRepository nhanVienPKTRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Page<TaiKhoan> listAll(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return taiKhoanRepository.searchAccounts(keyword, pageable);
        }
        return taiKhoanRepository.findAll(pageable);
    }

    public Optional<TaiKhoan> getTaiKhoanById(Integer maTK) {
        return taiKhoanRepository.findById(maTK);
    }

    public Optional<TaiKhoan> getTaiKhoanByUsername(String username) {
        return taiKhoanRepository.findByUsername(username);
    }

    @Transactional
    public TaiKhoan saveTaiKhoan(TaiKhoan taiKhoan, String rawPassword) {
        if (taiKhoan.getUsername() == null || taiKhoan.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (taiKhoan.getQuyen() == null || taiKhoan.getQuyen().getMaQuyen() == null) {
            throw new IllegalArgumentException("Quyền không được để trống.");
        }
        if (taiKhoan.getLoaiTK() == null || taiKhoan.getLoaiTK().trim().isEmpty()) {
            throw new IllegalArgumentException("Loại tài khoản không được để trống.");
        }
        
        // Kiểm tra mã TK tồn tại khi thêm mới (chỉ khi có maTK)
        boolean isNew = taiKhoan.getMaTK() == null || !taiKhoanRepository.existsById(taiKhoan.getMaTK());
        if (isNew) {
            // Nếu là tài khoản mới, mật khẩu là bắt buộc
            if (rawPassword == null || rawPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Mật khẩu không được để trống khi tạo mới tài khoản.");
            }
            taiKhoan.setPassword(passwordEncoder.encode(rawPassword)); // Mã hóa mật khẩu mới
        } else {
            // Nếu là cập nhật và có cung cấp mật khẩu mới
            if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                taiKhoan.setPassword(passwordEncoder.encode(rawPassword)); // Mã hóa mật khẩu mới
            } else {
                // Giữ lại mật khẩu cũ nếu không cung cấp mật khẩu mới
                TaiKhoan existingTaiKhoan = taiKhoanRepository.findById(taiKhoan.getMaTK())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại để cập nhật mật khẩu."));
                taiKhoan.setPassword(existingTaiKhoan.getPassword());
            }
        }
        return taiKhoanRepository.save(taiKhoan);
    }

    @Transactional
    public void deleteTaiKhoanById(Integer maTK) {
        if (!taiKhoanRepository.existsById(maTK)) {
            throw new RuntimeException("Tài khoản không tồn tại: " + maTK);
        }
        // Kiểm tra ràng buộc trước khi xóa (quan trọng!)
        if (sinhVienRepository.existsByTaiKhoan_MaTK(maTK) ||
            giangVienRepository.existsByTaiKhoan_MaTK(maTK) ||
            nhanVienPKTRepository.existsByTaiKhoan_MaTK(maTK)) {
            throw new RuntimeException("Không thể xóa tài khoản '" + maTK + "' vì đang được sử dụng bởi Sinh viên, Giảng viên hoặc Nhân viên.");
        }
        taiKhoanRepository.deleteById(maTK);
    }

    public List<Quyen> getAllQuyen() {
        return quyenRepository.findAll();
    }

    public Map<String, Map<String, String>> getUserDetailsMap(Page<TaiKhoan> taiKhoanPage) {
        Map<String, Map<String, String>> userMaps = new HashMap<>();
        for (TaiKhoan tk : taiKhoanPage.getContent()) {
            Map<String, String> details = new HashMap<>();
            String hoTen = "N/A";
            String email = "Không có";

            if ("SINHVIEN".equalsIgnoreCase(tk.getLoaiTK())) {
                Optional<SinhVien> svOpt = sinhVienRepository.findByTaiKhoan_MaTK(tk.getMaTK());
                if (svOpt.isPresent()) {
                    SinhVien sv = svOpt.get();
                    hoTen = sv.getHo() + " " + sv.getTen();
                    email = sv.getEmail() != null ? sv.getEmail() : "Chưa có";
                } else {
                     hoTen = "SV chưa liên kết";
                }
            } else if ("GIANGVIEN".equalsIgnoreCase(tk.getLoaiTK())) {
                Optional<GiangVien> gvOpt = giangVienRepository.findByTaiKhoan_MaTK(tk.getMaTK());
                 if (gvOpt.isPresent()) {
                    GiangVien gv = gvOpt.get();
                    hoTen = gv.getHo() + " " + gv.getTen();
                } else {
                     hoTen = "GV chưa liên kết";
                }
            } else if ("NHANVIENPKT".equalsIgnoreCase(tk.getLoaiTK())) {
                Optional<NhanVienPKT> nvOpt = nhanVienPKTRepository.findByTaiKhoan_MaTK(tk.getMaTK());
                 if (nvOpt.isPresent()) {
                    NhanVienPKT nv = nvOpt.get();
                    hoTen = nv.getHo() + " " + nv.getTen();
                } else {
                    hoTen = "NV chưa liên kết";
                }
            } else if ("ADMIN".equalsIgnoreCase(tk.getLoaiTK())) {
                 hoTen = "Quản trị viên hệ thống";
                 email = "";
            }
            details.put("hoTen", hoTen);
            details.put("email", email);
            userMaps.put(tk.getMaTK().toString(), details);
        }
        return userMaps;
    }
    
    public boolean existsById(Integer maTK) {
        return taiKhoanRepository.existsById(maTK);
    }
    
    // Lấy danh sách sinh viên chưa có tài khoản
    public List<SinhVien> getSinhVienKhongCoTaiKhoan() {
        return sinhVienRepository.findByTaiKhoanIsNull();
    }
    
    // Lấy danh sách giảng viên chưa có tài khoản
    public List<GiangVien> getGiangVienKhongCoTaiKhoan() {
        return giangVienRepository.findByTaiKhoanIsNull();
    }
    
    // Lấy danh sách nhân viên chưa có tài khoản
    public List<NhanVienPKT> getNhanVienKhongCoTaiKhoan() {
        return nhanVienPKTRepository.findByTaiKhoanIsNull();
    }
    
    @Transactional
    public void capTaiKhoan(String userRole, String selectedUserId, String selectedUserName, String selectedUserEmail, Integer quyenId) {
        try {
            System.out.println("=== SERVICE CẤP TÀI KHOẢN ===");
            System.out.println("userRole: " + userRole);
            System.out.println("selectedUserId: " + selectedUserId);
            System.out.println("selectedUserName: " + selectedUserName);
            System.out.println("quyenId: " + quyenId);
            
            // Tạo username và password dựa trên thông tin user
            String username = generateUsername(selectedUserName, userRole);
            String password = generatePassword(selectedUserId, userRole);
            
            System.out.println("Generated username: " + username);
            System.out.println("Generated password: " + password);
            
            // Kiểm tra username đã tồn tại chưa
            if (taiKhoanRepository.findByUsername(username).isPresent()) {
                throw new RuntimeException("Tên đăng nhập đã tồn tại: " + username);
            }
            
            // Tạo tài khoản mới (MaTK sẽ auto-generate)
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setUsername(username);
            taiKhoan.setPassword(passwordEncoder.encode(password));
            taiKhoan.setLoaiTK(userRole);
            
            System.out.println("Tìm quyền với ID: " + quyenId);
            // Set quyền
            Quyen quyen = quyenRepository.findById(quyenId)
                .orElseThrow(() -> new RuntimeException("Quyền không tồn tại với ID: " + quyenId));
            taiKhoan.setQuyen(quyen);
            
            System.out.println("Tìm thấy quyền: " + quyen.getTenQuyen());
            
            // Lưu tài khoản
            System.out.println("Đang lưu tài khoản...");
            TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);
            System.out.println("Đã lưu tài khoản với ID: " + savedTaiKhoan.getMaTK());
            
            // Cập nhật user tương ứng
            System.out.println("Đang cập nhật user...");
            updateUserWithAccount(userRole, selectedUserId, savedTaiKhoan);
            System.out.println("Đã cập nhật user thành công");
            
            // Gửi email thông báo (tạm thời comment)
            // sendAccountEmail(selectedUserEmail, username, password);
            
        } catch (Exception e) {
            System.err.println("Lỗi trong service cấp tài khoản: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cấp tài khoản: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void capTaiKhoanWithCredentials(String userRole, String selectedUserId, String selectedUserName, String selectedUserEmail, String username, String password, Integer quyenId) {
        try {
            // Kiểm tra username đã tồn tại chưa
            if (taiKhoanRepository.findByUsername(username).isPresent()) {
                throw new RuntimeException("Tên đăng nhập đã tồn tại: " + username);
            }
            
            // Tạo tài khoản mới (để auto-generate ID)
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setUsername(username);
            taiKhoan.setPassword(passwordEncoder.encode(password));
            taiKhoan.setLoaiTK(userRole);
            
            // Set quyền
            Quyen quyen = quyenRepository.findById(quyenId)
                .orElseThrow(() -> new RuntimeException("Quyền không tồn tại"));
            taiKhoan.setQuyen(quyen);
            
            // Lưu tài khoản
            taiKhoanRepository.save(taiKhoan);
            
            // Cập nhật user tương ứng
            updateUserWithAccount(userRole, selectedUserId, taiKhoan);
            
            // Gửi email thông báo (tạm thời comment)
            // sendAccountEmail(selectedUserEmail, username, password);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cấp tài khoản: " + e.getMessage(), e);
        }
    }
    

    
    private String generateUsername(String fullName, String role) {
        // Chuẩn hóa tên (bỏ dấu, viết thường)
        String normalizedName = normalizeString(fullName);
        
        // Lấy ngày tháng hiện tại để tránh trùng
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMM"));
        
        String domain;
        switch (role.toUpperCase()) {
            case "SINHVIEN":
                domain = "@student.ptithcm.edu.vn";
                break;
            case "GIANGVIEN":
                domain = "@ptithcm.edu.vn";
                break;
            case "NHANVIENPKT":
                domain = "@ptithcm.vn";
                break;
            default:
                domain = "@ptithcm.edu.vn";
        }
        
        return normalizedName + dateString + domain;
    }
    
    private String generatePassword(String userId, String role) {
        // Lấy thông tin ngày sinh từ user
        LocalDate birthDate = getUserBirthDate(userId, role);
        if (birthDate != null) {
            return birthDate.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        // Fallback: sử dụng ngày hiện tại
        return LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    }
    
    private LocalDate getUserBirthDate(String userId, String role) {
        switch (role.toUpperCase()) {
            case "SINHVIEN":
                Optional<SinhVien> sv = sinhVienRepository.findById(userId);
                return sv.map(SinhVien::getNgaySinh).orElse(null);
            case "GIANGVIEN":
                Optional<GiangVien> gv = giangVienRepository.findById(userId);
                return gv.map(GiangVien::getNgaySinh).orElse(null);
            case "NHANVIENPKT":
                Optional<NhanVienPKT> nv = nhanVienPKTRepository.findById(userId);
                return nv.map(NhanVienPKT::getNgaySinh).orElse(null);
            default:
                return null;
        }
    }
    
    private String normalizeString(String input) {
        if (input == null) return "";
        
        // Bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        
        // Chuyển thành chữ thường, bỏ khoảng trắng
        return normalized.toLowerCase().replaceAll("\\s+", "");
    }
    
    private void updateUserWithAccount(String userRole, String userId, TaiKhoan taiKhoan) {
        // Thêm debug logs
        System.out.println("=== UPDATE USER WITH ACCOUNT ===");
        System.out.println("userRole: [" + userRole + "]");
        System.out.println("userId: [" + userId + "]");
        System.out.println("userId length: " + (userId != null ? userId.length() : "null"));
        System.out.println("taiKhoan.getMaTK(): " + taiKhoan.getMaTK());
        
        // Trim và validate userId
        if (userId == null || userId.trim().isEmpty()) {
            throw new RuntimeException("User ID không được để trống");
        }
        final String trimmedUserId = userId.trim();
        System.out.println("userId after trim: [" + trimmedUserId + "]");
        
        switch (userRole.toUpperCase()) {
            case "SINHVIEN":
                System.out.println("Đang tìm sinh viên với ID: [" + trimmedUserId + "]");
                // Kiểm tra sinh viên có tồn tại không trước
                boolean sinhVienExists = sinhVienRepository.existsById(trimmedUserId);
                System.out.println("Sinh viên tồn tại: " + sinhVienExists);
                
                if (!sinhVienExists) {
                    // Debug: List tất cả sinh viên để so sánh
                    System.out.println("=== DANH SÁCH TẤT CẢ SINH VIÊN ===");
                    sinhVienRepository.findAll().forEach(sv -> {
                        System.out.println("- MaSV: [" + sv.getMaSV() + "] (length: " + sv.getMaSV().length() + ")");
                    });
                    throw new RuntimeException("Sinh viên không tồn tại với ID: [" + trimmedUserId + "]");
                }
                
                SinhVien sv = sinhVienRepository.findById(trimmedUserId)
                    .orElseThrow(() -> new RuntimeException("Sinh viên không tồn tại với ID: [" + trimmedUserId + "]"));
                System.out.println("Tìm thấy sinh viên: " + sv.getHo() + " " + sv.getTen());
                sv.setTaiKhoan(taiKhoan);
                sinhVienRepository.save(sv);
                System.out.println("Đã cập nhật tài khoản cho sinh viên");
                break;
                
            case "GIANGVIEN":
                System.out.println("Đang tìm giảng viên với ID: [" + trimmedUserId + "]");
                boolean giangVienExists = giangVienRepository.existsById(trimmedUserId);
                System.out.println("Giảng viên tồn tại: " + giangVienExists);
                
                if (!giangVienExists) {
                    System.out.println("=== DANH SÁCH TẤT CẢ GIẢNG VIÊN ===");
                    giangVienRepository.findAll().forEach(gv -> {
                        System.out.println("- MaGV: [" + gv.getMaGV() + "] (length: " + gv.getMaGV().length() + ")");
                    });
                    throw new RuntimeException("Giảng viên không tồn tại với ID: [" + trimmedUserId + "]");
                }
                
                GiangVien gv = giangVienRepository.findById(trimmedUserId)
                    .orElseThrow(() -> new RuntimeException("Giảng viên không tồn tại với ID: [" + trimmedUserId + "]"));
                System.out.println("Tìm thấy giảng viên: " + gv.getHo() + " " + gv.getTen());
                gv.setTaiKhoan(taiKhoan);
                giangVienRepository.save(gv);
                System.out.println("Đã cập nhật tài khoản cho giảng viên");
                break;
                
            case "NHANVIENPKT":
                System.out.println("Đang tìm nhân viên với ID: [" + trimmedUserId + "]");
                boolean nhanVienExists = nhanVienPKTRepository.existsById(trimmedUserId);
                System.out.println("Nhân viên tồn tại: " + nhanVienExists);
                
                if (!nhanVienExists) {
                    System.out.println("=== DANH SÁCH TẤT CẢ NHÂN VIÊN ===");
                    nhanVienPKTRepository.findAll().forEach(nv -> {
                        System.out.println("- MaNV: [" + nv.getMaNV() + "] (length: " + nv.getMaNV().length() + ")");
                    });
                    throw new RuntimeException("Nhân viên không tồn tại với ID: [" + trimmedUserId + "]");
                }
                
                NhanVienPKT nv = nhanVienPKTRepository.findById(trimmedUserId)
                    .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại với ID: [" + trimmedUserId + "]"));
                System.out.println("Tìm thấy nhân viên: " + nv.getHo() + " " + nv.getTen());
                nv.setTaiKhoan(taiKhoan);
                nhanVienPKTRepository.save(nv);
                System.out.println("Đã cập nhật tài khoản cho nhân viên");
                break;
                
            default:
                throw new RuntimeException("Loại user không hợp lệ: " + userRole);
        }
        System.out.println("=== CẬP NHẬT USER THÀNH CÔNG ===");
    }
} 