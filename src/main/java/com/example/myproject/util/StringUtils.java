package com.example.myproject.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {
    
    private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]");
    
    /**
     * Loại bỏ dấu tiếng Việt và chuyển về chữ thường
     */
    public static String removeDiacritics(String str) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }
        
        // Chuẩn hóa Unicode và loại bỏ dấu
        String normalized = Normalizer.normalize(str.trim(), Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITICS_AND_FRIENDS.matcher(normalized).replaceAll("");
        
        // Xử lý các ký tự đặc biệt của tiếng Việt
        withoutDiacritics = withoutDiacritics
            .replace("đ", "d")
            .replace("Đ", "d")
            .toLowerCase();
            
        return withoutDiacritics;
    }
    
    /**
     * Kiểm tra xem chuỗi tìm kiếm có khớp với chuỗi đích không (không phân biệt dấu)
     */
    public static boolean containsIgnoreDiacritics(String source, String search) {
        if (source == null || search == null) {
            return false;
        }
        
        String normalizedSource = removeDiacritics(source);
        String normalizedSearch = removeDiacritics(search);
        
        return normalizedSource.contains(normalizedSearch);
    }
} 