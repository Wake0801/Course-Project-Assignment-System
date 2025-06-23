package com.example.myproject.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myproject.dto.ThongKeDiemDTO;
import com.example.myproject.dto.ThongKePhanBoDiemDTO;
import com.example.myproject.entity.LopTinChi;
import com.example.myproject.service.ThongKeService;

@Controller
@RequestMapping("/nvpkt/thong-ke")
public class ThongKeController {
    
    @Autowired
    private ThongKeService thongKeService;
    
    @GetMapping("/diem")
    public String thongKeDiem(Model model,
                             @RequestParam(value = "maLopTC", required = false) String maLopTC,
                             @RequestParam(value = "loaiCham", required = false) String loaiCham,
                             @RequestParam(value = "search", required = false) String search) {
        
        // Lấy danh sách lớp tín chỉ
        List<LopTinChi> danhSachLopTC = thongKeService.getAllLopTinChi();
        model.addAttribute("danhSachLopTC", danhSachLopTC);
        
        // Lấy thông tin lớp tín chỉ được chọn
        if (maLopTC != null && !maLopTC.isEmpty()) {
            LopTinChi lopTinChi = thongKeService.getLopTinChiById(maLopTC);
            model.addAttribute("lopTinChiDaChon", lopTinChi);
            
            // Lấy dữ liệu thống kê điểm
            List<ThongKeDiemDTO> thongKeDiem = thongKeService.getThongKeDiem(maLopTC, loaiCham, search);
            model.addAttribute("thongKeDiem", thongKeDiem);
        }
        
        model.addAttribute("maLopTCDaChon", maLopTC);
        model.addAttribute("loaiChamDaChon", loaiCham);
        model.addAttribute("searchKeyword", search);
        
        return "nvpkt/thongKeDiem";
    }
    
    @GetMapping("/diem/export")
    public ResponseEntity<byte[]> exportExcel(@RequestParam("maLopTC") String maLopTC,
                                             @RequestParam(value = "loaiCham", required = false) String loaiCham,
                                             @RequestParam(value = "search", required = false) String search) {
        try {
            List<ThongKeDiemDTO> thongKeDiem = thongKeService.getThongKeDiem(maLopTC, loaiCham, search);
            LopTinChi lopTinChi = thongKeService.getLopTinChiById(maLopTC);
            
            // Tạo workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Thống kê điểm");
            
            // =================== TẠO CÁC STYLE ===================
            
            // Style cho tiêu đề chính (Header trường)
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.RED.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho tiêu đề phụ
            CellStyle subTitleStyle = workbook.createCellStyle();
            Font subTitleFont = workbook.createFont();
            subTitleFont.setBold(true);
            subTitleFont.setFontHeightInPoints((short) 14);
            subTitleStyle.setFont(subTitleFont);
            subTitleStyle.setAlignment(HorizontalAlignment.CENTER);
            subTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho thông tin báo cáo
            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setFontHeightInPoints((short) 11);
            infoStyle.setFont(infoFont);
            infoStyle.setAlignment(HorizontalAlignment.LEFT);
            
            // Style cho header table
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho số (centered)
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setBorderTop(BorderStyle.THIN);
            numberStyle.setBorderBottom(BorderStyle.THIN);
            numberStyle.setBorderLeft(BorderStyle.THIN);
            numberStyle.setBorderRight(BorderStyle.THIN);
            numberStyle.setAlignment(HorizontalAlignment.CENTER);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho điểm (highlighted)
            CellStyle scoreStyle = workbook.createCellStyle();
            Font scoreFont = workbook.createFont();
            scoreFont.setBold(true);
            scoreStyle.setFont(scoreFont);
            scoreStyle.setBorderTop(BorderStyle.THIN);
            scoreStyle.setBorderBottom(BorderStyle.THIN);
            scoreStyle.setBorderLeft(BorderStyle.THIN);
            scoreStyle.setBorderRight(BorderStyle.THIN);
            scoreStyle.setAlignment(HorizontalAlignment.CENTER);
            scoreStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // =================== TẠO HEADER ===================
            int currentRow = 0;
            
            // Logo và tên trường (Row 0-1)
            Row logoRow = sheet.createRow(currentRow++);
            Cell logoCell = logoRow.createCell(0);
            logoCell.setCellValue("HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG");
            logoCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            
            Row deptRow = sheet.createRow(currentRow++);
            Cell deptCell = deptRow.createCell(0);
            deptCell.setCellValue("KHOA CÔNG NGHỆ THÔNG TIN");
            deptCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
            
            // Dòng trống
            currentRow++;
            
            // Tiêu đề báo cáo (Row 3)
            Row reportTitleRow = sheet.createRow(currentRow++);
            Cell reportTitleCell = reportTitleRow.createCell(0);
            String reportTitle = "BÁO CÁO THỐNG KÊ ĐIỂM";
            if ("mid".equals(loaiCham)) {
                reportTitle += " GIỮA KỲ";
            } else if ("final".equals(loaiCham)) {
                reportTitle += " CUỐI KỲ";
            } else {
                reportTitle += " TỔNG KẾT";
            }
            reportTitleCell.setCellValue(reportTitle);
            reportTitleCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));
            
            // Dòng trống
            currentRow++;
            
            // Thông tin lớp học (Row 5-8)
            Row classInfoRow1 = sheet.createRow(currentRow++);
            Cell classInfoCell1 = classInfoRow1.createCell(0);
            classInfoCell1.setCellValue("Lớp tín chỉ: " + (lopTinChi != null ? lopTinChi.getMaLopTC() : maLopTC));
            classInfoCell1.setCellStyle(infoStyle);
            
            if (lopTinChi != null) {
                Row classInfoRow2 = sheet.createRow(currentRow++);
                Cell classInfoCell2 = classInfoRow2.createCell(0);
                classInfoCell2.setCellValue("Môn học: " + lopTinChi.getMonHoc().getTenMon());
                classInfoCell2.setCellStyle(infoStyle);
                
                Row classInfoRow3 = sheet.createRow(currentRow++);
                Cell classInfoCell3 = classInfoRow3.createCell(0);
                classInfoCell3.setCellValue("Giảng viên: " + lopTinChi.getGiangVien().getHo() + " " + lopTinChi.getGiangVien().getTen());
                classInfoCell3.setCellStyle(infoStyle);
            }
            
            Row dateRow = sheet.createRow(currentRow++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Ngày xuất báo cáo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            dateCell.setCellStyle(infoStyle);
            
            // Dòng trống
            currentRow++;
            
            // =================== TẠO BẢNG DỮ LIỆU ===================
            
            // Header của bảng
            Row headerRow = sheet.createRow(currentRow++);
            String[] headers = {"STT", "Mã đề tài", "Tên đề tài", "Nhóm", "Mã SV", "Tên SV", "Lớp"};
            
            // Thêm cột điểm dựa trên loại chấm
            String scoreColumnName;
            if ("mid".equals(loaiCham)) {
                scoreColumnName = "Điểm giữa kì";
            } else if ("final".equals(loaiCham)) {
                scoreColumnName = "Điểm cuối kì";
            } else {
                scoreColumnName = "Tổng điểm";
            }
            
            // Tạo array mới với cột điểm
            String[] fullHeaders = new String[headers.length + 1];
            System.arraycopy(headers, 0, fullHeaders, 0, headers.length);
            fullHeaders[headers.length] = scoreColumnName;
            
            for (int i = 0; i < fullHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fullHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Dữ liệu
            int stt = 1;
            for (ThongKeDiemDTO dto : thongKeDiem) {
                for (ThongKeDiemDTO.ThanhVienNhomDTO tv : dto.getThanhViens()) {
                    Row row = sheet.createRow(currentRow++);
                    
                    // STT
                    Cell sttCell = row.createCell(0);
                    sttCell.setCellValue(stt++);
                    sttCell.setCellStyle(numberStyle);
                    
                    // Mã đề tài
                    Cell maDTCell = row.createCell(1);
                    maDTCell.setCellValue(dto.getMaDT());
                    maDTCell.setCellStyle(dataStyle);
                    
                    // Tên đề tài
                    Cell tenDTCell = row.createCell(2);
                    tenDTCell.setCellValue(dto.getTenDT());
                    tenDTCell.setCellStyle(dataStyle);
                    
                    // Tên nhóm
                    Cell tenNhomCell = row.createCell(3);
                    tenNhomCell.setCellValue(dto.getTenNhom());
                    tenNhomCell.setCellStyle(dataStyle);
                    
                    // Mã SV
                    Cell maSVCell = row.createCell(4);
                    maSVCell.setCellValue(tv.getMaSV());
                    maSVCell.setCellStyle(dataStyle);
                    
                    // Tên SV
                    Cell tenSVCell = row.createCell(5);
                    tenSVCell.setCellValue(tv.getTenSV());
                    tenSVCell.setCellStyle(dataStyle);
                    
                    // Lớp SV
                    Cell lopSVCell = row.createCell(6);
                    lopSVCell.setCellValue(tv.getLopSV() != null ? tv.getLopSV() : "");
                    lopSVCell.setCellStyle(dataStyle);
                    
                    // Điểm
                    Cell scoreCell = row.createCell(7);
                    if ("mid".equals(loaiCham) && tv.getDiemGiuaKy() != null) {
                        scoreCell.setCellValue(tv.getDiemGiuaKy());
                    } else if ("final".equals(loaiCham) && tv.getDiemCuoiKy() != null) {
                        scoreCell.setCellValue(tv.getDiemCuoiKy());
                    } else if (tv.getDiemTongKet() != null) {
                        scoreCell.setCellValue(tv.getDiemTongKet());
                    } else {
                        scoreCell.setCellValue("");
                    }
                    scoreCell.setCellStyle(scoreStyle);
                }
            }
            
            // =================== THÊM CHỮ KÝ ===================
            currentRow += 2; // Dòng trống
            
            Row signatureHeaderRow = sheet.createRow(currentRow++);
            Cell leftSignatureCell = signatureHeaderRow.createCell(1);
            leftSignatureCell.setCellValue("NGƯỜI LẬP");
            leftSignatureCell.setCellStyle(subTitleStyle);
            
            Cell rightSignatureCell = signatureHeaderRow.createCell(5);
            rightSignatureCell.setCellValue("TRƯỞNG KHOA");
            rightSignatureCell.setCellStyle(subTitleStyle);
            
            currentRow += 4; // Khoảng trống cho chữ ký
            
            Row signatureNameRow = sheet.createRow(currentRow);
            Cell leftNameCell = signatureNameRow.createCell(1);
            leftNameCell.setCellValue("(Ký và ghi rõ họ tên)");
            leftNameCell.setCellStyle(infoStyle);
            
            Cell rightNameCell = signatureNameRow.createCell(5);
            rightNameCell.setCellValue("(Ký và ghi rõ họ tên)");
            rightNameCell.setCellStyle(infoStyle);
            
            // =================== TỰ ĐỘNG ĐIỀU CHỈNH KÍCH THƯỚC CỘT ===================
            for (int i = 0; i < fullHeaders.length; i++) {
                sheet.autoSizeColumn(i);
                // Đặt width tối thiểu và tối đa
                int currentWidth = sheet.getColumnWidth(i);
                if (currentWidth < 2000) { // Tối thiểu
                    sheet.setColumnWidth(i, 2000);
                } else if (currentWidth > 8000) { // Tối đa
                    sheet.setColumnWidth(i, 8000);
                }
            }
            
            // Điều chỉnh chiều cao các row tiêu đề
            sheet.getRow(0).setHeightInPoints(25); // Logo row
            sheet.getRow(1).setHeightInPoints(20); // Department row
            sheet.getRow(3).setHeightInPoints(20); // Report title row
            
            // Xuất file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            // Tạo tên file với thời gian
            String fileName = String.format("BaoCaoThongKeDiem_%s_%s_%s.xlsx", 
                lopTinChi != null ? lopTinChi.getMaLopTC().replaceAll("[^a-zA-Z0-9]", "_") : maLopTC,
                loaiCham != null ? loaiCham : "TongKet",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            HttpHeaders headersResponse = new HttpHeaders();
            headersResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersResponse.setContentDispositionFormData("attachment", fileName);
            
            return new ResponseEntity<>(outputStream.toByteArray(), headersResponse, HttpStatus.OK);
            
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/ty-le-diem")
    public String thongKeTyLeDiem(Model model,
                                  @RequestParam(value = "maLopTC", required = false) String maLopTC,
                                  @RequestParam(value = "loaiCham", required = false) String loaiCham) {
        
        // Lấy danh sách lớp tín chỉ
        List<LopTinChi> danhSachLopTC = thongKeService.getAllLopTinChi();
        model.addAttribute("danhSachLopTC", danhSachLopTC);
        
        // Lấy thông tin phân bố điểm
        if (maLopTC != null && !maLopTC.isEmpty()) {
            ThongKePhanBoDiemDTO phanBoDiem = thongKeService.getPhanBoDiem(maLopTC, loaiCham);
            model.addAttribute("phanBoDiem", phanBoDiem);
        }
        
        model.addAttribute("maLopTCDaChon", maLopTC);
        model.addAttribute("loaiChamDaChon", loaiCham);
        
        return "nvpkt/thongKeTyLeDiem";
    }
    
    @GetMapping("/ty-le-diem/export")
    public ResponseEntity<byte[]> exportPhanBoDiemExcel(@RequestParam("maLopTC") String maLopTC,
                                                        @RequestParam(value = "loaiCham", required = false) String loaiCham) {
        try {
            ThongKePhanBoDiemDTO phanBoDiem = thongKeService.getPhanBoDiem(maLopTC, loaiCham);
            LopTinChi lopTinChi = thongKeService.getLopTinChiById(maLopTC);
            
            // Tạo workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Phân bố điểm");
            
            // =================== TẠO CÁC STYLE ===================
            
            // Style cho tiêu đề chính
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.RED.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho tiêu đề phụ
            CellStyle subTitleStyle = workbook.createCellStyle();
            Font subTitleFont = workbook.createFont();
            subTitleFont.setBold(true);
            subTitleFont.setFontHeightInPoints((short) 14);
            subTitleStyle.setFont(subTitleFont);
            subTitleStyle.setAlignment(HorizontalAlignment.CENTER);
            subTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho thông tin báo cáo
            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setFontHeightInPoints((short) 11);
            infoStyle.setFont(infoFont);
            infoStyle.setAlignment(HorizontalAlignment.LEFT);
            
            // Style cho header table
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // Style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // =================== TẠO HEADER ===================
            int currentRow = 0;
            
            // Logo và tên trường
            Row logoRow = sheet.createRow(currentRow++);
            Cell logoCell = logoRow.createCell(0);
            logoCell.setCellValue("HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG CƠ SỞ TPHCM");
            logoCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
            
            Row deptRow = sheet.createRow(currentRow++);
            Cell deptCell = deptRow.createCell(0);
            deptCell.setCellValue("KHOA CÔNG NGHỆ THÔNG TIN 2");
            deptCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
            
            // Dòng trống
            currentRow++;
            
            // Tiêu đề báo cáo
            Row reportTitleRow = sheet.createRow(currentRow++);
            Cell reportTitleCell = reportTitleRow.createCell(0);
            String reportTitle = "BÁO CÁO PHÂN BỐ ĐIỂM";
            if ("mid".equals(loaiCham)) {
                reportTitle += " GIỮA KỲ";
            } else if ("final".equals(loaiCham)) {
                reportTitle += " CUỐI KỲ";
            } else {
                reportTitle += " TỔNG KẾT";
            }
            reportTitleCell.setCellValue(reportTitle);
            reportTitleCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 3));
            
            // Dòng trống
            currentRow++;
            
            // Thông tin lớp học
            Row classInfoRow1 = sheet.createRow(currentRow++);
            Cell classInfoCell1 = classInfoRow1.createCell(0);
            classInfoCell1.setCellValue("Lớp tín chỉ: " + (lopTinChi != null ? lopTinChi.getMaLopTC() : maLopTC));
            classInfoCell1.setCellStyle(infoStyle);
            
            if (lopTinChi != null) {
                Row classInfoRow2 = sheet.createRow(currentRow++);
                Cell classInfoCell2 = classInfoRow2.createCell(0);
                classInfoCell2.setCellValue("Môn học: " + lopTinChi.getMonHoc().getTenMon());
                classInfoCell2.setCellStyle(infoStyle);
                
                Row classInfoRow3 = sheet.createRow(currentRow++);
                Cell classInfoCell3 = classInfoRow3.createCell(0);
                classInfoCell3.setCellValue("Giảng viên: " + lopTinChi.getGiangVien().getHo() + " " + lopTinChi.getGiangVien().getTen());
                classInfoCell3.setCellStyle(infoStyle);
            }
            
            Row dateRow = sheet.createRow(currentRow++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Ngày xuất báo cáo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            dateCell.setCellStyle(infoStyle);
            
            // Dòng trống
            currentRow++;
            
            // =================== TẠO BẢNG PHÂN BỐ ĐIỂM ===================
            
            // Header của bảng
            Row headerRow = sheet.createRow(currentRow++);
            String[] headers = {"Khoảng điểm", "Số sinh viên", "Tỷ lệ (%)", "Xếp loại"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Dữ liệu phân bố điểm
            if (phanBoDiem != null && phanBoDiem.getPhanBoDiem() != null) {
                for (ThongKePhanBoDiemDTO.KhoangDiemDTO khoang : phanBoDiem.getPhanBoDiem()) {
                    Row row = sheet.createRow(currentRow++);
                    
                    // Khoảng điểm
                    Cell khoangCell = row.createCell(0);
                    khoangCell.setCellValue(khoang.getKhoangDiem());
                    khoangCell.setCellStyle(dataStyle);
                    
                    // Số sinh viên
                    Cell soSVCell = row.createCell(1);
                    soSVCell.setCellValue(khoang.getSoLuongSinhVien());
                    soSVCell.setCellStyle(dataStyle);
                    
                    // Tỷ lệ
                    Cell tyLeCell = row.createCell(2);
                    tyLeCell.setCellValue(String.format("%.1f%%", khoang.getTyLe()));
                    tyLeCell.setCellStyle(dataStyle);
                    
                    // Xếp loại
                    Cell xepLoaiCell = row.createCell(3);
                    String xepLoai = "";
                    if (khoang.getKhoangDiem().contains("9.0")) {
                        xepLoai = "Xuất sắc";
                    } else if (khoang.getKhoangDiem().contains("8.0")) {
                        xepLoai = "Giỏi";
                    } else if (khoang.getKhoangDiem().contains("7.0")) {
                        xepLoai = "Khá";
                    } else if (khoang.getKhoangDiem().contains("6.0")) {
                        xepLoai = "Trung bình khá";
                    } else if (khoang.getKhoangDiem().contains("5.0")) {
                        xepLoai = "Trung bình";
                    } else {
                        xepLoai = "Yếu";
                    }
                    xepLoaiCell.setCellValue(xepLoai);
                    xepLoaiCell.setCellStyle(dataStyle);
                }
                
                // Dòng tổng
                currentRow++;
                Row totalRow = sheet.createRow(currentRow++);
                
                Cell totalLabelCell = totalRow.createCell(0);
                totalLabelCell.setCellValue("TỔNG CỘNG");
                totalLabelCell.setCellStyle(headerStyle);
                
                Cell totalCountCell = totalRow.createCell(1);
                totalCountCell.setCellValue(phanBoDiem.getTongSinhVien());
                totalCountCell.setCellStyle(headerStyle);
                
                Cell totalPercentCell = totalRow.createCell(2);
                totalPercentCell.setCellValue("100.0%");
                totalPercentCell.setCellStyle(headerStyle);
                
                Cell totalClassCell = totalRow.createCell(3);
                totalClassCell.setCellValue("-");
                totalClassCell.setCellStyle(headerStyle);
            }
            
            // =================== THÊM CHỮ KÝ ===================
            currentRow += 2;
            
            Row signatureHeaderRow = sheet.createRow(currentRow++);
            Cell leftSignatureCell = signatureHeaderRow.createCell(0);
            leftSignatureCell.setCellValue("NGƯỜI LẬP");
            leftSignatureCell.setCellStyle(subTitleStyle);
            
            Cell rightSignatureCell = signatureHeaderRow.createCell(2);
            rightSignatureCell.setCellValue("TRƯỞNG KHOA");
            rightSignatureCell.setCellStyle(subTitleStyle);
            
            currentRow += 4;
            
            Row signatureNameRow = sheet.createRow(currentRow);
            Cell leftNameCell = signatureNameRow.createCell(0);
            leftNameCell.setCellValue("(Ký và ghi rõ họ tên)");
            leftNameCell.setCellStyle(infoStyle);
            
            Cell rightNameCell = signatureNameRow.createCell(2);
            rightNameCell.setCellValue("(Ký và ghi rõ họ tên)");
            rightNameCell.setCellStyle(infoStyle);
            
            // =================== TỰ ĐỘNG ĐIỀU CHỈNH KÍCH THƯỚC CỘT ===================
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                if (currentWidth < 3000) {
                    sheet.setColumnWidth(i, 3000);
                } else if (currentWidth > 8000) {
                    sheet.setColumnWidth(i, 8000);
                }
            }
            
            // Điều chỉnh chiều cao các row tiêu đề
            sheet.getRow(0).setHeightInPoints(25);
            sheet.getRow(1).setHeightInPoints(20);
            sheet.getRow(3).setHeightInPoints(20);
            
            // Xuất file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            // Tạo tên file
            String fileName = String.format("BaoCaoPhanBoDiem_%s_%s_%s.xlsx", 
                lopTinChi != null ? lopTinChi.getMaLopTC().replaceAll("[^a-zA-Z0-9]", "_") : maLopTC,
                loaiCham != null ? loaiCham : "TongKet",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            HttpHeaders headersResponse = new HttpHeaders();
            headersResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersResponse.setContentDispositionFormData("attachment", fileName);
            
            return new ResponseEntity<>(outputStream.toByteArray(), headersResponse, HttpStatus.OK);
            
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 
