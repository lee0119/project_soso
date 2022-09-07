package com.example.soso.repository;

import com.example.soso.domain.Comment;
import com.example.soso.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);
}
