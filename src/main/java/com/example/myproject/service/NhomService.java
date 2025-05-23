package com.example.myproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.myproject.entity.Nhom;
import com.example.myproject.repository.NhomRepository;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class NhomService {

    @Autowired
    private NhomRepository nhomRepository;

    public Page<Nhom> findNhom(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        Page<Nhom> result = (keyword != null && !keyword.trim().isEmpty()) 
            ? nhomRepository.findByKeyword(keyword.toLowerCase(), pageable)
            : nhomRepository.findAllWithMemberCount(pageable);
            
        result.getContent().forEach(nhom -> 
            nhom.setSoThanhVienHienTai(getGroupMemberCount(nhom.getMaNhom())));
            
        return result;
    }

    public Optional<Nhom> findById(String maNhom) {
        return nhomRepository.findById(maNhom)
            .map(nhom -> {
                nhom.setSoThanhVienHienTai(getGroupMemberCount(maNhom));
                return nhom;
            });
    }

    public boolean existsById(String maNhom) {
        return maNhom != null && nhomRepository.existsById(maNhom);
    }

    public Long getGroupMemberCount(String maNhom) {
        return maNhom != null ? nhomRepository.countMembersByMaNhom(maNhom) : 0L;
    }

    public Nhom save(Nhom nhom) {
        validateNhom(nhom);
        
        // Nếu đang cập nhật và mã nhóm trống, đây là nhóm mới
        // Lưu và để database tự tạo mã
        Nhom savedNhom = nhomRepository.save(nhom);
        
        // Đảm bảo cập nhật số thành viên hiện tại
        if (savedNhom.getMaNhom() != null) {
            savedNhom.setSoThanhVienHienTai(getGroupMemberCount(savedNhom.getMaNhom()));
        }
        
        return savedNhom;
    }
    
    private void validateNhom(Nhom nhom) {
        if (nhom == null) {
            throw new IllegalArgumentException("Nhóm không được để trống");
        }
        
        String tenNhom = nhom.getTenNhom();
        if (tenNhom == null || tenNhom.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhóm không được để trống");
        }

        if (nhom.getSoLuongTVToiDa() <= 0) {
            throw new IllegalArgumentException("Số lượng thành viên tối đa phải lớn hơn 0");
        }
        
        // Kiểm tra số thành viên hiện tại chỉ khi cập nhật (mã nhóm đã tồn tại)
        String maNhom = nhom.getMaNhom();
        if (maNhom != null && !maNhom.trim().isEmpty()) {
            Long currentMembers = getGroupMemberCount(maNhom);
            if (nhom.getSoLuongTVToiDa() < currentMembers) {
                throw new IllegalArgumentException("Số lượng thành viên tối đa không thể nhỏ hơn số thành viên hiện tại (" + currentMembers + ")");
            }
        }
    }

    private boolean isUpdateMode(Nhom nhom) {
        return nhom != null && nhom.getMaNhom() != null && !nhom.getMaNhom().trim().isEmpty() && existsById(nhom.getMaNhom());
    }

    public void deleteById(String maNhom) {
        if (!existsById(maNhom)) {
            throw new RuntimeException("Nhóm không tồn tại!");
        }
        
        Long memberCount = getGroupMemberCount(maNhom);
        if (memberCount > 0) {
            throw new RuntimeException("Không thể xóa nhóm vì vẫn còn " + memberCount + " sinh viên trong nhóm");
        }
        
        nhomRepository.deleteById(maNhom);
    }
}