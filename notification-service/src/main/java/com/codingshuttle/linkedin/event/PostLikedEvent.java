package com.codingshuttle.linkedin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikedEvent {

    private Long creatorId;
    private Long likedByUserId;
    private Long postId;

}
