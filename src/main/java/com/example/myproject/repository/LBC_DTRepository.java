package com.example.myproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.myproject.compositeKey.LoaiBaoCaoDeTaiId;
import com.example.myproject.entity.LoaiBaoCaoDeTai;

@Repository
public interface LBC_DTRepository extends JpaRepository<LoaiBaoCaoDeTai, LoaiBaoCaoDeTaiId> {

    @Query("SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.id.maDT = :maDT")
    List<LoaiBaoCaoDeTai> findById_MaDT(@Param("maDT") int maDT);
    @Query("SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.maDT = :maDT AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao")
    Optional<LoaiBaoCaoDeTai> findByDeTaiAndLoaiBaoCao(@Param("maDT") int maDT, @Param("maLoaiBaoCao") int maLoaiBaoCao);

    @Query("SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.lopTinChi.maLopTC = :maLopTC")
    List<LoaiBaoCaoDeTai> findByLopTinChi(@Param("maLopTC") String maLopTC);

    @Query("SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.lopTinChi.maLopTC = :maLopTC AND lbc.loaiBaoCao.maLoaiBaoCao = :maLoaiBaoCao")
    List<LoaiBaoCaoDeTai> findByLopTinChiAndLoaiBaoCao(@Param("maLopTC") String maLopTC, @Param("maLoaiBaoCao") int maLoaiBaoCao);

    @Query("SELECT lbc FROM LoaiBaoCaoDeTai lbc WHERE lbc.deTai.nhom.maNhom = :maNhom")
    List<LoaiBaoCaoDeTai> findByNhom(@Param("maNhom") int maNhom);
}
