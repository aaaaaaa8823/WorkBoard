package com.kind.backend.service;

import com.kind.backend.dto.request.CreateCommentRequest;
import com.kind.backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse create(Long postId, CreateCommentRequest request);
    List<CommentResponse> listByPost(Long postId);
    void delete(Long commentId);
}
