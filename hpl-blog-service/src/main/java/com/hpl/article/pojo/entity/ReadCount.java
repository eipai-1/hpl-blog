package com.hpl.article.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * fixme 访问计数，后续改用redis替换
 *
 * @author YiHui
 * @date 2022/8/25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("read_count")
public class ReadCount extends CommonEntity {

    /** 文档ID（文章/评论） */
    private Long documentId;

    /** 文档类型：1-文章，2-评论 */
    private Integer documentType;

    /** 访问计数 */
    private Integer cnt;

}
