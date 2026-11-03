package com.kind.backend.controller;

import com.kind.backend.dto.request.CreateCommentRequest;
import com.kind.backend.dto.response.CommentResponse;
import com.kind.backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public CommentResponse create(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return commentService.create(postId, request);
    }

    @GetMapping("/api/posts/{postId}/comments")
    public List<CommentResponse> list(@PathVariable Long postId) {
        return commentService.listByPost(postId);
    }

    @DeleteMapping("/api/comments/{id}")
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}