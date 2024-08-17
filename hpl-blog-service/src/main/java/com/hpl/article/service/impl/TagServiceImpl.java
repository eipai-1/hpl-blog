package com.hpl.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.Tag;
import com.hpl.article.pojo.enums.PublishStatusEnum;
import com.hpl.article.mapper.TagMapper;
import com.hpl.article.service.TagService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.redis.RedisClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Resource
    private RedisClient redisClient;


//    /**
//     * 查询标签信息，支持分页和关键词搜索。
//     *
//     * @param key 搜索关键词，用于匹配标签名。
//     * @param pageParam 分页参数，包含页码和每页数量。
//     * @return 返回包含标签数据的分页对象。
//     */
//    @Override
//    public CommonPageVo<TagDTO> queryTags(String key, CommonPageParam pageParam) {
//        // 构建查询条件，查询在线且未被删除的标签，支持按关键词搜索和按标签ID降序排序
//        LambdaQueryWrapper<Tag> query = Wrappers.lambdaQuery();
//        query.eq(Tag::getStatus, PublishStatusEnum.PUBLISHED.getCode())
//                .eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
//                .and(StringUtils.isNotBlank(key), v -> v.like(Tag::getTagName, key))
//                .orderByDesc(Tag::getId);
//        // 如果提供了分页参数，则添加分页限制
//        if (pageParam != null) {
//            query.last(CommonPageParam.getLimitSql(pageParam));
//        }
//        // 执行查询，获取标签列表
//        List<Tag> tags = tagMapper.selectList(query);
//
//        // 将标签实体转换为DTO对象
//        List<TagDTO> tagDTOS = tags.stream()
//                .map(this::tagToDTO)
//                .collect(Collectors.toList());
//
//        // 计算总记录数，用于分页
//        LambdaQueryWrapper<Tag> wrapper = Wrappers.lambdaQuery();
//        wrapper.eq(Tag::getStatus, PublishStatusEnum.PUBLISHED.getCode())
//                .eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
//                .and(StringUtils.isNotBlank(key), v -> v.like(Tag::getTagName, key));
//        Long totalCount = tagMapper.selectCount(wrapper);
//
//        // 构建并返回分页响应对象
//        return CommonPageVo.build(tagDTOS, pageParam.getPageSize(), pageParam.getPageNum(), totalCount);
//    }

    @Override
    public List<TagDTO> getAllTags(){

        List<TagDTO> res= redisClient.getList("tag:all", TagDTO.class);
        if (res!=null){
            return res;
        }

        List<Tag> tags=lambdaQuery()
                .eq(Tag::getStatus, PublishStatusEnum.PUBLISHED.getCode())
                .eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
                .orderByDesc(Tag::getId)
                .list();

        res = tags.stream()
                .map(this::tagToDTO)
                .collect(Collectors.toList());

        redisClient.set("tag:all",res,1L, TimeUnit.DAYS);
        return res;
    }


    private TagDTO tagToDTO(Tag tag){
        if (tag == null) {
            return null;
        }
        TagDTO dto = new TagDTO();
        dto.setTagName(tag.getTagName());
        dto.setTagId(tag.getId());

        return dto;
    }

    /**
     * 根据id查询标签
     *
     * @param tagId
     * @return
     */
    @Override
    public TagDTO getById(Long tagId){
        return this.getAllTags().stream()
                .filter(t -> t.getTagId().equals(tagId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据标签名称查询标签ID。
     *
     * @param tag 标签名称
     * @return 如果找到匹配的标签，则返回标签的ID；否则返回null。
     */
    @Override
    public Long getIdByName(String tag) {
        return this.getAllTags().stream()
                .filter(t -> t.getTagName().equals(tag))
                .map(TagDTO::getTagId)
                .findFirst()
                .orElse(null);
    }
}
