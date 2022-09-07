package com.example.soso.repository;

import com.example.soso.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();
    Optional<Post> findById(Long id);
}
