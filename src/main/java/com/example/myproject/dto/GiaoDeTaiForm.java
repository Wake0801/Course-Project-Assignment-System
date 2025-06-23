package com.example.myproject.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class GiaoDeTaiForm {
    private String maLopTC;
    private Integer maLoaiBaoCao;
    private Map<Integer, Integer> giaoNhom;
}
