package com.kind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long postId;
    private String body;
    private String status;
    private Long authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
}