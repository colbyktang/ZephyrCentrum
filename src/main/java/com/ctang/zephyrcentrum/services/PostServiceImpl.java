package com.ctang.zephyrcentrum.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ctang.zephyrcentrum.models.Post;
import com.ctang.zephyrcentrum.repositories.PostRepository;

import jakarta.transaction.Transactional;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    @Override
    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public List<Post> getAllPostsByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    @Transactional
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public boolean deletePost(Long id) {
        postRepository.deleteById(id);
        if (postRepository.findById(id).isPresent()) {
            return false;
        }
        return true;
    }
}
