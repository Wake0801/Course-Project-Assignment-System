package com.example.myproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myproject.entity.LoaiBaoCao;

@Repository
public interface LoaiBaoCaoRepository extends JpaRepository<LoaiBaoCao, String> {

}
