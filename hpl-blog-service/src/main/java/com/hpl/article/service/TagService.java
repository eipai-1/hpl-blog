package com.hpl.article.service;


import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.Tag;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageVo;

import java.util.List;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
public interface TagService {

    /**
     * 根据id查询标签列表
     * @param tagId
     * @return
     */
    List<Tag> getListById(Long tagId);

    CommonPageVo<TagDTO> queryTags(String key, CommonPageParam pageParam);

    Long queryTagId(String tag);
}
