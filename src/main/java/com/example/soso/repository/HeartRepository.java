package com.example.soso.repository;

import com.example.soso.domain.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByPostId(Long postId);
}
