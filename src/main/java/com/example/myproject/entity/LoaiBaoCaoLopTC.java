package com.example.myproject.entity;

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

    private Double heSoDiem;
}

