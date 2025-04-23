package com.ctang.zephyrcentrum.services;

import java.util.List;

import com.ctang.zephyrcentrum.models.Post;

public interface PostService {
    Post createPost(Post post);
    Post getPostById(Long id);
    List<Post> getAllPostsByUserId(Long userId);
    List<Post> getAllPosts();
    Post updatePost(Post post);
    boolean deletePost(Long id);
}
