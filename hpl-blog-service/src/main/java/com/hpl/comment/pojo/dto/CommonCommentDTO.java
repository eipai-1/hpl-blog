package com.hpl.comment.pojo.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @author : rbe
 * @date : 2024/7/10 10:55
 */
@Data
public class CommonCommentDTO implements Comparable<CommonCommentDTO> {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户图像
     */
    private String userPhoto;

    /**
     * 评论时间
     */
    private Long commentTime;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 点赞数量
     */
    private Integer praiseCount;

    /**
     * true 表示已经点赞
     */
    private Boolean praised;

    @Override
    public int compareTo(@NotNull CommonCommentDTO o) {
        return Long.compare(o.getCommentTime(), this.commentTime);
    }
}