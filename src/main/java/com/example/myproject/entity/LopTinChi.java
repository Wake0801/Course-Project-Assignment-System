package com.example.myproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "LopTinChi")
public class LopTinChi {
    @Id
    @Column(name = "MaLopTC", length = 10)
    private String maLopTC;
    
    @Transient
    private String tenLopTC;

    @ManyToOne
    @JoinColumn(name = "MaMon", nullable = false)
    private MonHoc monHoc;

    @ManyToOne
    @JoinColumn(name = "MaGV", nullable = false)
    private GiangVien giangVien;
    
    /**
     * Phương thức tự động tạo tên lớp tín chỉ từ mã và tên môn học
     * @return Tên lớp tín chỉ được tạo động
     */
    public String getTenLopTC() {
        if (this.monHoc != null) {
            return this.maLopTC + " - " + this.monHoc.getTenMon();
        }
        return this.maLopTC;
    }
}