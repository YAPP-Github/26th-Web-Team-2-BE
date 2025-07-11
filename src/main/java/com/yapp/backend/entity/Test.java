package com.yapp.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "test")
public class Test {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메타데이터
    private String urlTest;

}
