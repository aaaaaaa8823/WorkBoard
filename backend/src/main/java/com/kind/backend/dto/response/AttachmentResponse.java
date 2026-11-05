package com.kind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AttachmentResponse {
    private Long id;
    private Long postId;
    private Long commentId;
    private String originalName;
    private String contentType;
    private Long sizeBytes;
    private Long uploaderId;
    private String uploaderUsername;
    private String downloadUrl;
    private LocalDateTime createdAt;
}
