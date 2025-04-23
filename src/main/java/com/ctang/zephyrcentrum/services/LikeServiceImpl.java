package com.ctang.zephyrcentrum.services;

import org.springframework.stereotype.Service;

import com.ctang.zephyrcentrum.repositories.LikeRepository;
import com.ctang.zephyrcentrum.models.Like;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    public LikeServiceImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public boolean isPostLiked(Long userId, Long postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    @Override
    public void likePost(Long userId, Long postId) {
        // likeRepository.save(new Like(userId, postId));
        
    }

    @Override
    public void unlikePost(Long userId, Long postId) {
        likeRepository.deleteByUserIdAndPostId(userId, postId);
    }

    @Override
    public int getPostLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
    
}
