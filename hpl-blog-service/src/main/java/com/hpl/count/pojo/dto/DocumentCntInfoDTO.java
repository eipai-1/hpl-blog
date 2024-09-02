package com.hpl.count.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : rbe
 * @date : 2024/9/1 22:19
 */
@Data
@Builder
public class DocumentCntInfoDTO {

    /** 文档点赞数 */
    private Integer  praiseCount;

    /** 文档阅读数 */
    private Integer  readCount;

    /** 文档收藏数 */
    private Integer  collectionCount;

    /** 文档评论数 */
    private Integer commentCount;
}
