package com.ctang.zephyrcentrum.services;

public interface LikeService {
    void likePost(Long userId, Long postId);
    void unlikePost(Long userId, Long postId);
    boolean isPostLiked(Long userId, Long postId);
    int getPostLikeCount(Long postId);
}
