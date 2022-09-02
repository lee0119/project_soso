package com.project.soso.repository;

import com.project.soso.entity.UploadImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;

public interface ImageRepository extends JpaRepository<UploadImage, Long> {
    UploadImage findByImgUrl(String imgUrl);
}
