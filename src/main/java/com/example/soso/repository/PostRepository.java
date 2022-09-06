package com.example.soso.repository;

import com.example.soso.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
=======

>>>>>>> 662a55560bc07d664388a66946b308995fba5354
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();
    Optional<Post> findById(Long id);
<<<<<<< HEAD
}
=======
}
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
