package com.hpl.statistic.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : rbe
 * @date : 2024/8/2 8:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("read_count")
public class ReadCount extends CommonEntity {

    private Long documentId;

    private Integer documentType;

    private Integer cnt;


}
