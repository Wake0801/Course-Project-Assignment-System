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
@Table(name = "LoaiBaoCao")
public class LoaiBaoCao {
    @Id
    @Column(name = "MaLoaiBaoCao", length = 10)
    private String maLoaiBaoCao;

    @Column(name = "TenLoaiBaoCao", nullable = false, length = 50)
    private String tenLoaiBaoCao;

}