package com.project.soso.entity;

import com.project.soso.dto.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.awt.*;
import java.util.List;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String imgUrl;

    @Column
    private String fileName;



    public Post(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.imgUrl = postRequestDto.getImgUrl();
        this.fileName = postRequestDto.getFileName();
    }


    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.imgUrl = postRequestDto.getImgUrl();
        this.fileName = postRequestDto.getFileName();
    }

}
