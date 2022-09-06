package com.example.soso.domain;

<<<<<<< HEAD
import com.example.soso.dto.PostRequestDto;
=======
import com.example.soso.dto.request.PostRequestDto;
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import javax.persistence.*;

=======

import javax.persistence.*;
>>>>>>> 662a55560bc07d664388a66946b308995fba5354

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
    private String imageUrl;

    @Column
    private String fileName;



    public Post(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.imageUrl = postRequestDto.getImageUrl();
        this.fileName = postRequestDto.getFileName();
    }


    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.imageUrl = postRequestDto.getImageUrl();
        this.fileName = postRequestDto.getFileName();
    }

<<<<<<< HEAD
}
=======
}
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
