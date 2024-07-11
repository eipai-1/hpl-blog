package com.hpl.comment.pojo.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/10 10:54
 */
@Data
public class TopCommentDTO extends CommonCommentDTO {
    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 子评论
     */
    private List<SubCommentDTO> childComments;

    public List<SubCommentDTO> getChildComments() {
        if (childComments == null) {
            childComments = new ArrayList<>();
        }
        return childComments;
    }

    @Override
    public int compareTo(@NotNull CommonCommentDTO o) {
        return Long.compare(o.getCommentTime(), this.getCommentTime());
    }
}
