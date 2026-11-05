package com.kind.backend.repository;

import com.kind.backend.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByPostIdOrderByCreatedAtAsc(Long postId);
    List<Attachment> findByCommentIdOrderByCreatedAtAsc(Long commentId);
}
