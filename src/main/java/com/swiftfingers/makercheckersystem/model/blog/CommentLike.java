package com.swiftfingers.makercheckersystem.model.blog;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Created by Obiora on 08-Jul-2024 at 10:50
 */
@Entity
@Table(name = "comment_likes")
@Data
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

}
