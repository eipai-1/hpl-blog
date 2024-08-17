package com.hpl.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.Tag;

import java.util.List;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
public interface TagService extends IService<Tag> {

    List<TagDTO> getAllTags();

    /**
     * 根据id查询标签列表
     *
     * @param tagId
     * @return
     */
    TagDTO getById(Long tagId);

//    CommonPageVo<TagDTO> queryTags(String key, CommonPageParam pageParam);

    Long getIdByName(String tag);
}
