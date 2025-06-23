package com.example.myproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.myproject.compositeKey.LoaiBaoCaoLopTCId;

import com.example.myproject.entity.LoaiBaoCaoLopTC;

@Repository
public interface LBC_LTCRepository extends JpaRepository<LoaiBaoCaoLopTC, LoaiBaoCaoLopTCId> {

     @Query("SELECT l FROM LoaiBaoCaoLopTC l WHERE l.id.maLopTC = :maLopTC")
     List<LoaiBaoCaoLopTC> findByMaLopTC(@Param("maLopTC") String maLopTC);
     @Query("SELECT l FROM LoaiBaoCaoLopTC l WHERE l.id.maLopTC = :maLopTC AND l.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao")
     Optional<LoaiBaoCaoLopTC> findByMaLopTCAndLoaiBaoCao(@Param("maLopTC") String maLopTC, @Param("maLoaiBaoCao") int maLoaiBaoCao);
}    
