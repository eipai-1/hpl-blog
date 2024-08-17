package com.hpl.article.service.impl;

import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.mapper.ArticleTagMapper;
import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.ArticleTag;
import com.hpl.article.pojo.entity.Tag;
import com.hpl.article.service.ArticleTagService;
import com.hpl.article.service.TagService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.redis.RedisClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisClient redisClient;

    /**
     * 根据文章ID查询关联的标签信息。
     *
     * @param articleId 文章的唯一标识ID。
     * @return 返回包含标签信息的CommonPageVo对象，其中标签信息以TagDTO形式呈现。
     * CommonPageVo封装了分页信息和数据列表，这里只用到了数据列表部分。
     */
    @Override
    public List<TagDTO> getTagsByAId(Long articleId) {

        // redis

        String key = "articleTags:" + articleId;

        List<ArticleTag> articleTags = redisClient.getList(key, ArticleTag.class);
        if(articleTags == null){
            // 构建查询条件，查询与文章ID匹配且未被删除的文章标签信息
            LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(ArticleTag::getArticleId, articleId)
                    .eq(ArticleTag::getDeleted,0);
            // 根据查询条件，获取文章标签信息
            articleTags = articleTagMapper.selectList(queryWrapper);

            redisClient.set(key, articleTags, 60 * 24L, TimeUnit.MINUTES);
        }


        // 初始化用于存储标签DTO的列表
        List<TagDTO> tagsDTO = new ArrayList<>();
        // 如果找到了文章标签信息，则进一步查询对应的标签详情
        for (ArticleTag articleTag : articleTags){
            tagsDTO.add(tagService.getById(articleTag.getTagId()));
        }

        return tagsDTO;
    }


    @Override
    public void deleteTagByAId(Long articleId){
        lambdaUpdate().set(ArticleTag::getDeleted, CommonDeletedEnum.YES.getCode())
                .eq(ArticleTag::getArticleId, articleId)
                .update();
    }

    @Override
    public void saveTagByAId(Set<Long> tagIds, Long articleId){
        tagIds.forEach(tagId -> {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        });
    }
}
