package com.example.myproject.compositeKey;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiBaoCaoDeTaiId implements Serializable {
    private int maLoaiBaoCao;
    private int maDT;
}

