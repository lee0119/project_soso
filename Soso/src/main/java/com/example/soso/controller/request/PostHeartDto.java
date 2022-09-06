package com.example.soso.controller.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostHeartDto {
    private String postId;
    private String memberId;
}
