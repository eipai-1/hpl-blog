package com.hpl.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.mapper.ArticleTagMapper;
import com.hpl.article.pojo.entity.ArticleTag;
import com.hpl.article.pojo.entity.Tag;
import com.hpl.article.service.ArticleTagService;
import com.hpl.article.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/28 9:00
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private TagService tagService;

    /**
     * 根据文章ID查询关联的标签信息。
     *
     * @param articleId 文章的唯一标识ID。
     * @return 返回包含标签信息的CommonPageVo对象，其中标签信息以TagDTO形式呈现。
     * CommonPageVo封装了分页信息和数据列表，这里只用到了数据列表部分。
     */
    @Override
    public List<Tag> getTagsByAId(Long articleId) {
        // 初始化用于存储标签DTO的列表
        List<Tag> tags = new ArrayList<>();

        // 构建查询条件，查询与文章ID匹配且未被删除的文章标签信息
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, articleId)
                .eq(ArticleTag::getDeleted,0);
        // 根据查询条件，获取第一篇匹配的文章标签信息
//        ArticleTag articleTag = articleTagMapper.selectOne(queryWrapper);
        List<ArticleTag> articleTags = articleTagMapper.selectList(queryWrapper);
        List<Long> tagIds = articleTagMapper.selectList(queryWrapper).stream()
                .map(ArticleTag::getId).toList();

        // 如果找到了文章标签信息，则进一步查询对应的标签详情
        for (ArticleTag articleTag : articleTags){
            tags.add(tagService.getById(articleTag.getTagId()));
        }

//        if(tagIds!=null && !tagIds.isEmpty()){
//            // 根据标签ID，获取标签列表
//            tags= tagService.getById(articleTag.getTagId());
//
//
////            // 遍历标签列表，将每个标签的信息转换为TagDTO，并添加到tagDTOS列表中
////            for(Tag tag : tags){
////                TagDTO tagDTO = new TagDTO();
////                tagDTO.setTag(tag.getTagName());
////                tagDTO.setTagId(tag.getId());
////                tagDTO.setStatus(tag.getStatus());
////                tagDTOS.add(tagDTO);
////            }
//        }

        return tags;
    }



}
