package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.blog.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Obiora on 08-Jul-2024 at 15:30
 */
public interface BlogLikeRepository extends JpaRepository<BlogLike, Long> {
}
