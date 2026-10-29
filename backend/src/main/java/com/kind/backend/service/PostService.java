package com.kind.backend.service;

import com.kind.backend.dto.request.CreatePostRequest;
import com.kind.backend.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse create(CreatePostRequest request);
    PostResponse getById(Long id);
}

