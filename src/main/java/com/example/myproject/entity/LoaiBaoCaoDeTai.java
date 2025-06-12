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

    private LocalDate ngayBaoCao;

    private Double diem;
}

