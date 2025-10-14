package com.codingshuttle.linkedin.posts_service.controller;

import com.codingshuttle.linkedin.posts_service.dto.PostCreateRequestDto;
import com.codingshuttle.linkedin.posts_service.dto.PostDto;
import com.codingshuttle.linkedin.posts_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostsController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto,
                                              HttpServletRequest request) {
        log.info("Received request to create post: RequestId: {}", request.getAttribute("requestId"));
        PostDto postDto = postService.createPost(postCreateRequestDto, 1L);

        return ResponseEntity.status(CREATED).body(postDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        PostDto postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostForUser(@PathVariable Long userId) {
        List<PostDto> postDtos = postService.getAllPostsForUser(userId);
        return ResponseEntity.ok(postDtos);
    }
}
