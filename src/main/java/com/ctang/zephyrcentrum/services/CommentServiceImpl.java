package com.ctang.zephyrcentrum.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ctang.zephyrcentrum.models.Comment;
import com.ctang.zephyrcentrum.repositories.CommentRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAllCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public List<Comment> getAllCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, String content) {
        Comment comment = getCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        if (commentRepository.findById(commentId).isPresent()) {
            return false;
        }
        return true;
    }


}
