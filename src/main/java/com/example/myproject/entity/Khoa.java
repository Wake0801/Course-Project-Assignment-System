package com.example.myproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "Khoa")
public class Khoa {
    @Id
    @Column(name = "MaKhoa", length = 10)
    private String maKhoa;

    @Column(name = "TenKhoa", nullable = false, length = 100)
    private String tenKhoa;

}