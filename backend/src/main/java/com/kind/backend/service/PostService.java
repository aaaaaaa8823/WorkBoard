package com.kind.backend.service;

import com.kind.backend.dto.request.CreatePostRequest;
import com.kind.backend.dto.request.UpdatePostRequest;
import com.kind.backend.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse create(CreatePostRequest request);
    PostResponse getById(Long id);
    PostResponse update(Long id, UpdatePostRequest request);
    void delete(Long id);
    List<PostResponse> list(Long departmentId, String type, String search);
    //последние активные темы по createdAt
    List<PostResponse> getRecent(int limit);
    PostResponse restore(Long id);
}

