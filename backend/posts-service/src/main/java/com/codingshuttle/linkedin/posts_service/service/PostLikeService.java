package com.codingshuttle.linkedin.posts_service.service;

import com.codingshuttle.linkedin.event.PostLikedEvent;
import com.codingshuttle.linkedin.posts_service.entity.PostLike;
import com.codingshuttle.linkedin.posts_service.exception.BadRequestException;
import com.codingshuttle.linkedin.posts_service.exception.ResourceNotFoundException;
import com.codingshuttle.linkedin.posts_service.repository.PostLikeRepository;
import com.codingshuttle.linkedin.posts_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, PostLikedEvent> kafkaTemplate;

    public void likePost(Long postId, long userId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Optional<PostLike> postLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (postLike.isPresent()) {
            throw new BadRequestException("Cannot like a post more than once");
        }
        PostLike newLike = PostLike.builder().postId(postId).userId(userId).build();
        postLikeRepository.save(newLike);
        kafkaTemplate.send("post-liked-topic", PostLikedEvent.builder()
                .creatorId(userId)
                .likedByUserId(userId)
                .postId(postId)
                .build());
        log.info("Post with id {} liked by user with id {}", postId, userId);
    }

    public void unlikePost(Long postId, long userId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Optional<PostLike> postLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (postLike.isEmpty()) {
            throw new BadRequestException("Cannot unlike a post which is not liked");
        }
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Post with id {} unliked by user with id {}", postId, userId);
    }
}
