package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.blog.Blog;
import com.swiftfingers.makercheckersystem.payload.request.BlogRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Obiora on 08-Jul-2024 at 10:52
 */
public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("SELECT new com.swiftfingers.makercheckersystem.payload.request.BlogRequest(b.id, b.content, COUNT(c), COUNT(l)) FROM Blog b LEFT JOIN b.comments c LEFT JOIN b.likes l GROUP BY b.id, b.content")
    List<BlogRequest> findAllBlogWithCommentsAndLikes (Pageable pageable);

    @Query("SELECT new com.swiftfingers.makercheckersystem.payload.request.BlogRequest(b.id, b.content, COUNT(DISTINCT c.id), COUNT(DISTINCT l.id)) FROM Blog b LEFT JOIN b.comments c LEFT JOIN b.likes l GROUP BY b.id, b.content")
    List<BlogRequest> findAllBlogDTOsWithCommentsAndLikes(Pageable pageable);
}
