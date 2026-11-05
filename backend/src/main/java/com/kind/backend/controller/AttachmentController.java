package com.kind.backend.controller;

import com.kind.backend.dto.response.AttachmentResponse;
import com.kind.backend.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/api/posts/{postId}/attachments")
    public AttachmentResponse uploadForPost(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file
    ) {
        return attachmentService.uploadForPost(postId, file);
    }

    @PostMapping("/api/comments/{commentId}/attachments")
    public AttachmentResponse uploadForComment(
            @PathVariable Long commentId,
            @RequestParam("file") MultipartFile file
    ) {
        return attachmentService.uploadForComment(commentId, file);
    }

    @GetMapping("/api/posts/{postId}/attachments")
    public List<AttachmentResponse> listByPost(@PathVariable Long postId) {
        return attachmentService.listByPost(postId);
    }

    @GetMapping("/api/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Resource resource = attachmentService.loadAsResource(id);
        String name = attachmentService.getOriginalName(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + name + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/api/attachments/{id}")
    public void delete(@PathVariable Long id) {
        attachmentService.delete(id);
    }
}