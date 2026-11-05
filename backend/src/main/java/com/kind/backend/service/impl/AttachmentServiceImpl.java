package com.kind.backend.service.impl;

import com.kind.backend.dto.response.AttachmentResponse;
import com.kind.backend.model.Attachment;
import com.kind.backend.model.Comment;
import com.kind.backend.model.Post;
import com.kind.backend.model.User;
import com.kind.backend.repository.AttachmentRepository;
import com.kind.backend.repository.CommentRepository;
import com.kind.backend.repository.PostRepository;
import com.kind.backend.repository.UserRepository;
import com.kind.backend.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf");

    private final AttachmentRepository attachmentRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-bytes:5242880}")
    private long maxBytes;

    @Override
    @Transactional
    public AttachmentResponse uploadForPost(Long postId, MultipartFile file){
        User uploader = currentUser();
        Post post = postRepository.findByIdAndStatus(postId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Post not found"));

        validateFile(file);
        String storagePath = storeOnDisk(file);

        Attachment a = new Attachment();
        a.setPost(post);
        a.setComment(null);
        a.setUploader(uploader);
        a.setOriginalName(safeOriginalName(file));
        a.setStoragePath(storagePath);
        a.setContentType(file.getContentType());
        a.setSizeBytes(file.getSize());

        return toResponse(attachmentRepository.save(a));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getSize() > maxBytes) {
            throw new RuntimeException("File is too large");
        }
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_TYPES.contains(ct)) {
            throw new RuntimeException("File type not allowed: " + ct);
        }
    }

    private String storeOnDisk(MultipartFile file) {
        try {
            Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(root);

            String original = safeOriginalName(file);
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot); //pdf
            }
            String stored = UUID.randomUUID() + ext;
            Path target = root.resolve(stored);
            file.transferTo(target);
            return stored;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String safeOriginalName(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            return "file";
        }
        // убрать путь, если браузер прислал C:\fakepath\...
        name = Paths.get(name).getFileName().toString();
        return name.length() > 255 ? name.substring(0, 255) : name;
    }

    @Override
    @Transactional
    public AttachmentResponse uploadForComment(Long commentId, MultipartFile file) {
        User uploader = currentUser();
        Comment comment = commentRepository.findByIdAndStatus(commentId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        validateFile(file);
        String storagePath = storeOnDisk(file);

        Attachment a = new Attachment();
        a.setPost(null);
        a.setComment(comment);
        a.setUploader(uploader);
        a.setOriginalName(safeOriginalName(file));
        a.setStoragePath(storagePath);
        a.setContentType(file.getContentType());
        a.setSizeBytes(file.getSize());

        return toResponse(attachmentRepository.save(a));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> listByPost(Long postId) {
        return attachmentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> listByComment(Long commentId) {
        return attachmentRepository.findByCommentIdOrderByCreatedAtAsc(commentId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadAsResource(Long id) {
        Attachment a = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(a.getStoragePath()).normalize();
            // защита: файл только внутри uploadDir
            if (!path.startsWith(Paths.get(uploadDir).toAbsolutePath().normalize())) {
                throw new RuntimeException("Invalid path");
            }
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found on disk");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found on disk");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getOriginalName(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"))
                .getOriginalName();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Attachment a = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        User current = currentUser();
        boolean owner = a.getUploader().getId().equals(current.getId());
        boolean admin = "ADMIN".equalsIgnoreCase(current.getRole())
                || "admin".equalsIgnoreCase(current.getRole());
        if (!owner && !admin) {
            throw new RuntimeException("Not allowed");
        }

        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(a.getStoragePath()).normalize();
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
        attachmentRepository.delete(a);
    }

    private AttachmentResponse toResponse(Attachment a) {
        return new AttachmentResponse(
                a.getId(),
                a.getPost() != null ? a.getPost().getId() : null,
                a.getComment() != null ? a.getComment().getId() : null,
                a.getOriginalName(),
                a.getContentType(),
                a.getSizeBytes(),
                a.getUploader().getId(),
                a.getUploader().getUsername(),
                "/api/attachments/" + a.getId() + "/download",
                a.getCreatedAt()
        );
    }
}
