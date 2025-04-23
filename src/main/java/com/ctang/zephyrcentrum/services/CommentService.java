package com.ctang.zephyrcentrum.services;

import java.util.List;

import com.ctang.zephyrcentrum.models.Comment;

public interface CommentService {
    List<Comment> getAllCommentsByPostId(Long postId);
    List<Comment> getAllCommentsByUserId(Long userId);
    List<Comment> getAllComments();
    Comment getCommentById(Long commentId);
    Comment createComment(Comment comment);
    Comment updateComment(Long commentId, String content);
    boolean deleteComment(Long commentId);
}
