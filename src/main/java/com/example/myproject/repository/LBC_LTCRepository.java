package com.example.myproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myproject.compositeKey.LoaiBaoCaoLopTCId;

import com.example.myproject.entity.LoaiBaoCaoLopTC;

@Repository
public interface LBC_LTCRepository extends JpaRepository<LoaiBaoCaoLopTC, LoaiBaoCaoLopTCId> {

}
