package com.project.soso.entity;

import com.project.soso.dto.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


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

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String imgUrl;



    public Post(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.nickname = postRequestDto.getNickname();
        this.imgUrl = postRequestDto.getImgUrl();
    }


    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.nickname = postRequestDto.getNickname();
        this.imgUrl = postRequestDto.getImgUrl();
    }

}
