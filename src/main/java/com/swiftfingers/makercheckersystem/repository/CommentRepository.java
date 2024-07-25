package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.blog.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Obiora on 08-Jul-2024 at 10:58
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
