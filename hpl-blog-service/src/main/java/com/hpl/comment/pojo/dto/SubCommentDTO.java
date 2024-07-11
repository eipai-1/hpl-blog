package com.hpl.comment.pojo.dto;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * @author : rbe
 * @date : 2024/7/10 10:55
 */
@ToString(callSuper = true)
@Data
public class SubCommentDTO extends CommonCommentDTO {

    /**
     * 父评论内容
     */
    private String parentContent;


    @Override
    public int compareTo(@NotNull CommonCommentDTO o) {
        return Long.compare(this.getCommentTime(), o.getCommentTime());
    }
}

