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
@Table(name = "Quyen")
public class Quyen {
    @Id
    @Column(name = "MaQuyen", length = 10)
    private String maQuyen;

    @Column(name = "TenQuyen", nullable = false, length = 50)
    private String tenQuyen;

    @Column(name = "MoTaQuyen", length = 255)
    private String moTaQuyen;

}