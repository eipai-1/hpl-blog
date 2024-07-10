package com.hpl.article.service;


import com.hpl.article.dto.SearchTagDTO;
import com.hpl.article.dto.TagDTO;
import com.hpl.article.dto.TagPostDTO;
import com.hpl.pojo.CommonPageVo;

/**
 * 标签后台接口
 *
 * @author louzai
 * @date 2022-09-17
 */
public interface TagSettingService {

    void saveTag(TagPostDTO tagPostDTO);

    void deleteTag(Long tagId);

    void operateTag(Long tagId, Integer pushStatus);

    /**
     * 获取tag列表
     *
     * @param searchTagDTO
     * @return
     */
    CommonPageVo<TagDTO> getTagList(SearchTagDTO searchTagDTO);

    TagDTO getTagById(Long tagId);
}
