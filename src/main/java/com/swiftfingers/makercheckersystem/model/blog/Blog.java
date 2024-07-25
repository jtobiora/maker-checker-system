package com.swiftfingers.makercheckersystem.model.blog;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Obiora on 08-Jul-2024 at 10:35
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blogs")
@Data
public class Blog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "blog_id")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "blog_id")
    private List<BlogLike> likes = new ArrayList<>();
}
