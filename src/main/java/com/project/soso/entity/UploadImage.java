package com.project.soso.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class UploadImage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String imgUrl;

    public UploadImage(String filename, String imgUrl) {
        this.filename = filename;
        this.imgUrl = imgUrl;
    }

}
