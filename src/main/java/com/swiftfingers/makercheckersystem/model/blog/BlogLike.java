package com.swiftfingers.makercheckersystem.model.blog;

import com.swiftfingers.makercheckersystem.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Created by Obiora on 08-Jul-2024 at 10:38
 */
@Entity
@Table(name = "blog_likes")
@Data
public class BlogLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

}
