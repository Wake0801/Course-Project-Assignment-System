package com.example.myproject.repository;

import java.util.List;

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
}
