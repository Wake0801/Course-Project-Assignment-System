package com.example.myproject.repository;

import com.example.myproject.entity.LoaiBaoCao_LopTC;
import com.example.myproject.entity.LoaiBaoCao_LopTCPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LoaiBaoCao_LopTCRepository extends JpaRepository<LoaiBaoCao_LopTC, LoaiBaoCao_LopTCPK> {
    List<LoaiBaoCao_LopTC> findAllByMaLopTC(String maLopTC);

    @Transactional
    @Modifying
    void deleteByMaLopTCAndMaLoaiBaoCao(String maLopTC, int maLoaiBaoCao);
    // Thêm các phương thức custom nếu cần
}
