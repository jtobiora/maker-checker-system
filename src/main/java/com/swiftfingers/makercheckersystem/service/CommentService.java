package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.model.blog.Blog;
import com.swiftfingers.makercheckersystem.model.blog.Comment;
import com.swiftfingers.makercheckersystem.model.blog.CommentLike;
import com.swiftfingers.makercheckersystem.repository.BlogRepository;
import com.swiftfingers.makercheckersystem.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Obiora on 08-Jul-2024 at 10:57
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    private BlogRepository blogRepository;

    public Comment createComment(Long blogId, String content) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));
        Comment comment = new Comment();
        comment.setContent(content);
        blog.getComments().add(comment);
        blogRepository.save(blog);
        return blog.getComments().get(blog.getComments().size() - 1);
    }

    public Comment createReply(Long parentCommentId, String content) {
        Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        Comment reply = new Comment();
        reply.setContent(content);
        parentComment.getReplies().add(reply);
        commentRepository.save(parentComment);
        return parentComment.getReplies().get(parentComment.getReplies().size() - 1);
    }

    public CommentLike likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        CommentLike like = new CommentLike();
        like.setUserId(userId);
        comment.getLikes().add(like);
        return commentRepository.save(comment).getLikes().get(comment.getLikes().size() - 1);
    }

    public List<Comment> getAllCommentsByBlogId(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));
        return blog.getComments();
    }
}
