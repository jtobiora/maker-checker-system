package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.model.blog.Blog;
import com.swiftfingers.makercheckersystem.model.blog.BlogLike;
import com.swiftfingers.makercheckersystem.payload.request.BlogRequest;
import com.swiftfingers.makercheckersystem.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Obiora on 08-Jul-2024 at 10:59
 */
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public Blog createBlog(@RequestBody Blog blog) {
        return blogService.createBlog(blog);
    }

//    @GetMapping
//    public List<Blog> getAllBlogs() {
//        return blogService.getAllBlogs();
//    }

    @GetMapping
    public ResponseEntity<List<BlogRequest>> getAllBlogDTOs(Pageable pageable) {
//        List<BlogRequest> blogDTOs = blogService.getAllBlogs(pageable);
//        return new ResponseEntity<>(blogDTOs, HttpStatus.OK);

        List<BlogRequest> blogDTOs = blogService.getAllBlogDTOs(pageable);
        return new ResponseEntity<>(blogDTOs, HttpStatus.OK);
    }

    @PostMapping("/{blogId}/like")
    public BlogLike likeBlog(@PathVariable Long blogId, @RequestBody Long userId) {
        return blogService.likeBlog(blogId, userId);
    }
}
