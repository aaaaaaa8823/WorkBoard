package com.kind.backend.service;

import com.kind.backend.dto.response.AttachmentResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    AttachmentResponse uploadForPost(Long postId, MultipartFile file);
    AttachmentResponse uploadForComment(Long commentId, MultipartFile file);
    List<AttachmentResponse> listByPost(Long postId);
    List<AttachmentResponse> listByComment(Long commentId);
    Resource loadAsResource(Long id);
    String getOriginalName(Long id);
    void delete(Long id);
}
