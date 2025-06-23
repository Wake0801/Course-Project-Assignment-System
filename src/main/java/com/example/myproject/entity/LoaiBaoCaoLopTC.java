package com.example.myproject.entity;

import java.math.BigDecimal;

import com.example.myproject.compositeKey.LoaiBaoCaoLopTCId;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LoaiBaoCao_LopTC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiBaoCaoLopTC {

    @EmbeddedId
    private LoaiBaoCaoLopTCId id;
    @ManyToOne
    @JoinColumn(name = "MaLoaiBaoCao", referencedColumnName = "MaLoaiBaoCao", insertable = false, updatable = false)
    private LoaiBaoCao loaiBaoCao;
    @ManyToOne
    @JoinColumn(name = "MaLopTC", referencedColumnName = "MaLopTC", insertable = false, updatable = false)
    private LopTinChi maLopTC;
    private BigDecimal heSoDiem;
}

