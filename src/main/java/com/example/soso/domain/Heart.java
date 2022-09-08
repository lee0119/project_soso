package com.example.soso.domain;

import lombok.*;
import javax.persistence.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long postId;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;


    @Builder.Default
    @Column
    private boolean flag =false;

    public boolean validateMember(Member member) { return !this.member.equals(member); }
}
