package com.kind.backend.controller;

import com.kind.backend.dto.request.CreatePostRequest;
import com.kind.backend.dto.request.UpdatePostRequest;
import com.kind.backend.dto.response.PostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.kind.backend.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostResponse create(@Valid @RequestBody CreatePostRequest request) {
        return postService.create(request);
    }

    @GetMapping
    public List<PostResponse> list(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search
    ) {
        return postService.list(departmentId, type, search);
    }

    @GetMapping("/recent")
    public List<PostResponse> recent(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return postService.getRecent(limit);
    }

    @GetMapping("/{id}")
    public PostResponse getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    @PutMapping("/{id}")
    public PostResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        return postService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }

    @PostMapping("/{id}/restore")
    public PostResponse restore(@PathVariable Long id) {
        return postService.restore(id);
    }

}
