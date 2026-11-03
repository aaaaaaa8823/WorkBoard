package com.kind.backend.service.impl;

import com.kind.backend.dto.request.CreateCommentRequest;
import com.kind.backend.dto.response.CommentResponse;
import com.kind.backend.model.Comment;
import com.kind.backend.model.Post;
import com.kind.backend.model.User;
import com.kind.backend.repository.CommentRepository;
import com.kind.backend.repository.PostRepository;
import com.kind.backend.repository.UserRepository;
import com.kind.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse create(Long postId, CreateCommentRequest request){
        User author = currentUser();

        Post post = postRepository.findByIdAndStatus(postId, "ACTIVE").orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(author);

        String text = request.getBody() == null ? "" : request.getBody().trim();
        if (text.isEmpty()) {
            throw new RuntimeException("Comment body must not be empty");
        }
        comment.setBody(text);
        comment.setStatus("ACTIVE");

        return toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> listByPost(Long postId){
        postRepository.findByIdAndStatus(postId, "ACTIVE").orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository
                .findByPostIdAndStatusOrderByCreatedAtAsc(postId, "ACTIVE")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        assertCanModify(comment);

        comment.setStatus("DELETED");
        commentRepository.save(comment);
    }

    private void assertCanModify(Comment comment) {
        User current = currentUser();
        boolean author = comment.getAuthor().getId().equals(current.getId());
        boolean admin = "ADMIN".equalsIgnoreCase(current.getRole())
                || "admin".equalsIgnoreCase(current.getRole());

        if (!author && !admin) {
            throw new RuntimeException("Not allowed to delete this comment");
        }
    }

    private User currentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CommentResponse toResponse(Comment c){
        return new CommentResponse(
                c.getId(),
                c.getPost().getId(),
                c.getBody(),
                c.getStatus(),
                c.getAuthor().getId(),
                c.getAuthor().getUsername(),
                c.getCreatedAt()
        );
    }
}
