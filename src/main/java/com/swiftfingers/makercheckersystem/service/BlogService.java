package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.model.blog.Blog;
import com.swiftfingers.makercheckersystem.model.blog.BlogLike;
import com.swiftfingers.makercheckersystem.model.blog.Comment;
import com.swiftfingers.makercheckersystem.payload.request.BlogRequest;
import com.swiftfingers.makercheckersystem.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Obiora on 08-Jul-2024 at 10:51
 */
@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public Blog createBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public List<BlogRequest> getAllBlogs(Pageable pageable) {
        return blogRepository.findAllBlogDTOsWithCommentsAndLikes(pageable);
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
    }

    public BlogLike likeBlog(Long blogId, Long userId) {
        Blog blog = getBlogById(blogId);
        BlogLike like = new BlogLike();
        like.setUserId(userId);
        blog.getLikes().add(like);
        return blogRepository.save(blog).getLikes().get(blog.getLikes().size() - 1);
    }

    public List<BlogRequest> getAllBlogDTOs (Pageable pageable) {
        List<BlogRequest> blogDTOs = blogRepository.findAllBlogDTOsWithCommentsAndLikes(pageable);
        for (BlogRequest blogDTO : blogDTOs) {
            blogDTO.setTotalComments(countTotalComments(blogDTO.getId()));
        }
        return blogDTOs;
    }

    private Long countTotalComments(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) {
            return 0L;
        }
        return countComments(blog.getComments());
    }

    private Long countComments(List<Comment> comments) {
        Long count = (long) comments.size();
        for (Comment comment : comments) {
            count += countComments(comment.getReplies());
        }
        return count;
    }

}
