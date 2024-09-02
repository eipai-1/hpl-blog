package com.hpl.count.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/9/1 22:16
 */
@Data
@TableName("count")
public class Count implements Serializable {

    private Long id;

    private Long documentId;

    private Integer documentType;

    private Integer readCnt;

    private Integer praiseCnt;

    private Integer collectionCnt;

    private Integer commentCnt;


}
