package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "LoaiBaoCao_LopTC")
@IdClass(LoaiBaoCao_LopTCPK.class)
public class LoaiBaoCao_LopTC implements Serializable {
    @Id
    @Column(name = "MaLoaiBaoCao")
    private int maLoaiBaoCao;

    @Id
    @Column(name = "MaLopTC", length = 10)
    private String maLopTC;

    @Column(name = "HeSoDiem", precision = 3, scale = 2)
    private BigDecimal heSoDiem;
}
