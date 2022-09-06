package com.example.soso.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "heart")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostHeart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "post_id")
    @NonNull
    private String postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
