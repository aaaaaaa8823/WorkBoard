package com.kind.backend.repository;

import com.kind.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, String status);
    Optional<Comment> findByIdAndStatus(Long id, String status);
    long countByPostIdAndStatus(Long postId, String status);
}
