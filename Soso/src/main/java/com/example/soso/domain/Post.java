package com.example.soso.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;



    ////////////////////////////////////////////////////////////////
    //이부분부터 넣으면 됨
    //@ManyToOne 과 @OneToMany 로 양방향 관계
    //CascadeType.REMOVE로 게시글이 삭제되면 댓글도 삭제
//    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    private List<Comment> comments;


    private Boolean isHeart = false;

    public void like(){
        this.likeNum +=1;
    }
    public void dislike(){
        this.likeNum -=1;
    }

}

