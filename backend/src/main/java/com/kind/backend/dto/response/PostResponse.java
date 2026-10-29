package com.kind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String body;
    private String type;
    private String status;
    private Long authorId;
    private String authorUsername;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createAt;
}
