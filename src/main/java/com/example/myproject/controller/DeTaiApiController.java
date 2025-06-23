package com.example.myproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.myproject.entity.DeTai;
import com.example.myproject.entity.LoaiBaoCaoDeTai;
import com.example.myproject.entity.Nhom;
import com.example.myproject.repository.DeTaiRepository;
import com.example.myproject.repository.LBC_DTRepository;
import com.example.myproject.repository.NhomRepository;


@RestController
@RequestMapping("/api")
public class DeTaiApiController {
    @Autowired
    private DeTaiRepository deTaiRepository ;
    @Autowired
    private NhomRepository nhomRepository;
    @Autowired
    private LBC_DTRepository lbcDtRepository;
    @GetMapping("/nhom")
    public List<Nhom> getNhomByLopTC(@RequestParam String maLopTC) {
        return nhomRepository.findByLopTinChi_MaLopTC(maLopTC);
    }

    @GetMapping("/de-tai/diem")
    public List<LoaiBaoCaoDeTai> getDeTaiForDiem(
            @RequestParam String maLopTC,
            @RequestParam Integer maLoaiBaoCao) {
        return lbcDtRepository.findByLopTinChiAndLoaiBaoCao(maLopTC, maLoaiBaoCao);
    }
    public List<DeTai> getDeTaiChuaGiao(String maLopTC, Integer maLoaiBaoCao) {
    return deTaiRepository.findDeTaiChuaCoNhomByLopTCAndLoaiBaoCao(maLopTC, maLoaiBaoCao);
}

}