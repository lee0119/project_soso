package com.example.soso.repository;


import com.example.soso.domain.Member;
import com.example.soso.domain.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {

    Optional<PostHeart> findPostHeartByMemberAndPostId(Member member, String postId);
    Optional<List<PostHeart>> findPostHeartsByMemberId(String memberId);
}
