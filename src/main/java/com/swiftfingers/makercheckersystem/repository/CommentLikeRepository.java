package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.blog.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Obiora on 08-Jul-2024 at 15:31
 */
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
}