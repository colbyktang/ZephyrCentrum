package com.ctang.zephyrcentrum.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ctang.zephyrcentrum.models.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{
    List<Like> findByPostId(Long postId);
    List<Like> findByUserId(Long userId);
    int countByPostId(Long postId);
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
