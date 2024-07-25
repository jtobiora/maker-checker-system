package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.model.blog.Comment;
import com.swiftfingers.makercheckersystem.model.blog.CommentLike;
import com.swiftfingers.makercheckersystem.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Obiora on 08-Jul-2024 at 10:59
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Comment createComment(@RequestParam Long blogId, @RequestParam String content) {
        return commentService.createComment(blogId, content);
    }

    @PostMapping("/{parentId}/reply")
    public Comment createReply(@PathVariable Long parentId, @RequestParam String content) {
        return commentService.createReply(parentId, content);
    }

    @PostMapping("/{commentId}/like")
    public CommentLike likeComment(@PathVariable Long commentId, @RequestBody Long userId) {
        return commentService.likeComment(commentId, userId);
    }

    @GetMapping
    public List<Comment> getAllCommentsByBlogId(@RequestParam Long blogId) {
        return commentService.getAllCommentsByBlogId(blogId);
    }

}
