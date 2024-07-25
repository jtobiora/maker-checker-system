package com.swiftfingers.makercheckersystem.payload.request;

import lombok.Data;

/**
 * Created by Obiora on 09-Jul-2024 at 14:19
 */
@Data
public class BlogRequest {
    private Long id;
    private String content;
    private Long totalComments;
    private Long totalLikes;

    public BlogRequest (Long id, String content, Long totalComments, Long totalLikes) {
        this.id = id;
        this.content = content;
        this.totalComments = totalComments;
        this.totalLikes = totalLikes;
    }
}
