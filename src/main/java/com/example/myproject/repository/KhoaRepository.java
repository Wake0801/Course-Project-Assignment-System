package com.example.myproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myproject.entity.*;

@Repository
public interface KhoaRepository extends JpaRepository<Khoa, String> {

}
