package com.hpl.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.Tag;
import com.hpl.article.pojo.enums.PushStatusEnum;
import com.hpl.article.mapper.TagMapper;
import com.hpl.article.service.TagService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    /**
     * 根据id查询标签列表
     * @param tagId
     * @return
     */
    @Override
    public List<Tag> getListById(Long tagId){
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(Tag::getId,tagId)
                .eq(Tag::getDeleted,0);     //todo 表结构有个发布状态，这里不处理

        return tagMapper.selectList(queryWrapper);
    }

    /**
     * 查询标签信息，支持分页和关键词搜索。
     *
     * @param key 搜索关键词，用于匹配标签名。
     * @param pageParam 分页参数，包含页码和每页数量。
     * @return 返回包含标签数据的分页对象。
     */
    @Override
    public CommonPageVo<TagDTO> queryTags(String key, CommonPageParam pageParam) {
        // 构建查询条件，查询在线且未被删除的标签，支持按关键词搜索和按标签ID降序排序
        LambdaQueryWrapper<Tag> query = Wrappers.lambdaQuery();
        query.eq(Tag::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
                .and(StringUtils.isNotBlank(key), v -> v.like(Tag::getTagName, key))
                .orderByDesc(Tag::getId);
        // 如果提供了分页参数，则添加分页限制
        if (pageParam != null) {
            query.last(CommonPageParam.getLimitSql(pageParam));
        }
        // 执行查询，获取标签列表
        List<Tag> tags = tagMapper.selectList(query);

        // 将标签实体转换为DTO对象
        List<TagDTO> tagDTOS = tags.stream()
                .map(this::tagToDto)
                .collect(Collectors.toList());

        // 计算总记录数，用于分页
        LambdaQueryWrapper<Tag> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Tag::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
                .and(StringUtils.isNotBlank(key), v -> v.like(Tag::getTagName, key));
        Long totalCount = tagMapper.selectCount(wrapper);

        // 构建并返回分页响应对象
        return CommonPageVo.build(tagDTOS, pageParam.getPageSize(), pageParam.getPageNum(), totalCount);
    }



    private TagDTO tagToDto(Tag tag){
        if (tag == null) {
            return null;
        }
        TagDTO dto = new TagDTO();
        dto.setTag(tag.getTagName());
        dto.setTagId(tag.getId());
        dto.setStatus(tag.getStatus());
        return dto;
    }

    /**
     * 根据标签名称查询标签ID。
     *
     * @param tag 标签名称
     * @return 如果找到匹配的标签，则返回标签的ID；否则返回null。
     */
    @Override
    public Long queryTagId(String tag) {
        // 创建查询条件包装对象
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：选择ID列，筛选未删除的标签，且标签名称与参数tag匹配
        wrapper.select(Tag::getId)
                .eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Tag::getTagName, tag)
                .last("limit 1");

        // 根据查询条件查询标签信息
        Tag record = tagMapper.selectOne(wrapper);
        // 如果查询结果不为空，则返回标签ID；否则返回null
        return record != null ? record.getId() : null;
    }
}
