package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.example.myproject.compositeKey.LoaiBaoCaoDeTaiId;



@Entity
@Table(name = "LoaiBaoCao_DeTai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiBaoCaoDeTai {

    @EmbeddedId
    private LoaiBaoCaoDeTaiId id;
     @ManyToOne
    @JoinColumn(name = "MaLoaiBaoCao", referencedColumnName = "MaLoaiBaoCao", insertable = false, updatable = false)
    private LoaiBaoCao loaiBaoCao;

    @ManyToOne
    @JoinColumn(name = "maDT", referencedColumnName = "maDT", insertable = false, updatable = false)
    private DeTai deTai;
    
    private LocalDate ngayBaoCao;

    private Double diem;
}

