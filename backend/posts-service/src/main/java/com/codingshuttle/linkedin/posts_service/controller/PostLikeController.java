package com.codingshuttle.linkedin.posts_service.controller;

import com.codingshuttle.linkedin.posts_service.auth.UserContextHolder;
import com.codingshuttle.linkedin.posts_service.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Slf4j
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId){
        Long userId = UserContextHolder.getCurrentUserId();
        postLikeService.likePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId){
        Long userId = UserContextHolder.getCurrentUserId();
        postLikeService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

}
