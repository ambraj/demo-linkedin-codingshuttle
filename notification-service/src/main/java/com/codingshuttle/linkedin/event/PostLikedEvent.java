package com.codingshuttle.linkedin.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLikedEvent {

    private Long creatorId;
    private Long likedByUserId;
    private Long postId;

}
