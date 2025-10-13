package com.codingshuttle.linkedin.posts_service.controller;

import com.codingshuttle.linkedin.posts_service.dto.PostCreateRequestDto;
import com.codingshuttle.linkedin.posts_service.dto.PostDto;
import com.codingshuttle.linkedin.posts_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostsController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> post(@RequestBody PostCreateRequestDto postCreateRequestDto, HttpServletRequest request) {
        log.info("Received request to create post: {}", postCreateRequestDto);
        PostDto postDto = postService.createPost(postCreateRequestDto, 1L);

        return new ResponseEntity<>(postDto, CREATED);
    }

}
