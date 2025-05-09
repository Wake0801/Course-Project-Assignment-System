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
        if (nhom == null) {
            throw new IllegalArgumentException("Nhóm không được để trống");
        }

        String maNhom = nhom.getMaNhom();
        if (maNhom != null && !maNhom.trim().isEmpty() && !isUpdateMode(nhom) && existsById(maNhom)) {
            throw new RuntimeException("Mã nhóm '" + maNhom + "' đã tồn tại!");
        }
        
        return nhomRepository.save(nhom);
    }

    private boolean isUpdateMode(Nhom nhom) {
        return nhom != null && existsById(nhom.getMaNhom());
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