package com.swiftfingers.makercheckersystem.model.blog;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Obiora on 08-Jul-2024 at 10:36
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comments")
@Data
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_comment_id")
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private List<CommentLike> likes = new ArrayList<>();

}
