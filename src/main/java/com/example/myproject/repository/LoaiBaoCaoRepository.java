package com.example.myproject.repository;

import com.example.myproject.entity.LoaiBaoCao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiBaoCaoRepository extends JpaRepository<LoaiBaoCao, String> {
}
