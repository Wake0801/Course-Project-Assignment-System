package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "Lop")
public class Lop {
    @Id
    @Column(name = "MaLop", length = 10)
    private String maLop;

    @ManyToOne
    @JoinColumn(name = "MaKhoa", nullable = false)
    private Khoa khoa;

}