package com.hpl.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.ArticleTag;

import java.util.List;
import java.util.Set;

/**
 * @author : rbe
 * @date : 2024/7/28 9:00
 */
public interface ArticleTagService extends IService<ArticleTag> {

    /**
     * 根据文章ID查询关联的标签信息。
     *
     * @param articleId 文章的唯一标识ID。
     * @return 返回包含标签信息的CommonPageVo对象，其中标签信息以TagDTO形式呈现。
     * CommonPageVo封装了分页信息和数据列表，这里只用到了数据列表部分。
     */
    List<TagDTO> getTagsByAId(Long articleId);

    void deleteTagByAId(Long articleId);

    void saveTagByAId(Set<Long> tagIds, Long articleId);

}
