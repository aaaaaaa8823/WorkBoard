package com.kind.backend.service.impl;

import com.kind.backend.dto.request.CreatePostRequest;
import com.kind.backend.dto.request.UpdatePostRequest;
import com.kind.backend.dto.response.PostResponse;
import com.kind.backend.model.Department;
import com.kind.backend.model.Post;
import com.kind.backend.model.User;
import com.kind.backend.repository.DepartmentRepository;
import com.kind.backend.repository.PostRepository;
import com.kind.backend.repository.UserRepository;
import com.kind.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "PROJECT", "QUESTION", "INFO", "ANNOUNCEMENT", "IDEA"
    );

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public PostResponse create(CreatePostRequest request){
        User author = currentUser();

        String type = request.getType().trim().toUpperCase();

        if(!ALLOWED_TYPES.contains(type)){
            throw new RuntimeException("Invalid post type:" + request.getType());
        }

        Department department = null;
        if(request.getDepartmentId() != null){
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Post post = new Post();
        post.setTitle(request.getTitle().trim());
        post.setBody(request.getBody().trim());
        post.setType(type);
        post.setAuthor(author);
        post.setDepartment(department);
        post.setStatus("ACTIVE");

        return toResponse(postRepository.save(post));

    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(
                p.getId(),
                p.getTitle(),
                p.getBody(),
                p.getType(),
                p.getStatus(),
                p.getAuthor().getId(),
                p.getAuthor().getUsername(),
                p.getDepartment() != null ? p.getDepartment().getId() : null,
                p.getDepartment() != null ? p.getDepartment().getName() : null,
                p.getCreatedAt()
        );
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getById(Long id){
        Post post = postRepository.findByIdAndStatus(id, "ACTIVE").orElseThrow(
                () -> new RuntimeException("Post not found")
        );

        return toResponse(post);
    }

    @Override
    @Transactional
    public PostResponse update(Long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        assertCanModify(post);

        if (post.getStatus().equals("DELETED")) {
            throw new RuntimeException("Post is deleted");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            post.setTitle(request.getTitle().trim());
        }
        if (request.getBody() != null && !request.getBody().isBlank()) {
            post.setBody(request.getBody().trim());
        }
        if (request.getType() != null && !request.getType().isBlank()) {
            String type = request.getType().trim().toUpperCase();

            if (!ALLOWED_TYPES.contains(type)) {
                throw new RuntimeException("Invalid post type: " + request.getType());
            }
            post.setType(type);
        }

        //departmentId всегда с фронта при полном edit-форме
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            post.setDepartment(department);
        }

         if (Boolean.TRUE.equals(request.getClearDepartment())) {
             post.setDepartment(null);
         }

        return toResponse(postRepository.save(post));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        assertCanModify(post);

        post.setStatus("DELETED");
        postRepository.save(post);
    }

    private void assertCanModify(Post post) {
        User current = currentUser();
        boolean author = post.getAuthor().getId().equals(current.getId());
        boolean admin = "ADMIN".equalsIgnoreCase(current.getRole()) || "admin".equalsIgnoreCase(current.getRole());

        if (!author && !admin) {
            throw new RuntimeException("Not allowed to modify this post");
        }
    }

}
