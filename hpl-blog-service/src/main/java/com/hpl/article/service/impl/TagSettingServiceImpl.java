package com.hpl.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hpl.article.dto.SearchTagDTO;
import com.hpl.article.dto.TagDTO;
import com.hpl.article.dto.TagPostDTO;
import com.hpl.article.entity.Tag;
import com.hpl.article.mapper.TagMapper;
import com.hpl.article.service.TagSettingService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageVo;
import com.hpl.util.JsonUtil;
import com.hpl.util.NumUtil;
import com.hpl.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签后台接口
 *
 * @author louzai
 * @date 2022-09-17
 */
@Service
public class TagSettingServiceImpl implements TagSettingService {

    private static final String CACHE_TAG_PRE = "cache_tag_pre_";

    private static final Long CACHE_TAG_EXPRIE_TIME = 100L;

    @Autowired
    private TagMapper tagMapper;

    private Tag getById(Long tagId){
        LambdaQueryWrapper<Tag> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getId,tagId)
                .eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode());

        return tagMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTag(TagPostDTO tagPostDTO) {
        Tag tag = new Tag();
        if ( tagPostDTO != null ) {
            tag.setTagName( tagPostDTO.getTag() );
        }

        // 先写 MySQL
        if (NumUtil.eqZero(tagPostDTO.getTagId())) {
            tagMapper.insert(tag);
        } else {
            tag.setId(tagPostDTO.getTagId());
            tagMapper.updateById(tag);
        }

        // 再删除 Redis
        String redisKey = CACHE_TAG_PRE + tag.getId();
        RedisUtil.del(redisKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        Tag tag = this.getById(tagId);
        if (tag != null){
            // 先写 MySQL
            tagMapper.deleteById(tagId);

            // 再删除 Redis
            String redisKey = CACHE_TAG_PRE + tag.getId();
            RedisUtil.del(redisKey);
        }
    }

    @Override
    public void operateTag(Long tagId, Integer pushStatus) {
        Tag tag = this.getById(tagId);
        if (tag != null){

            // 先写 MySQL
            tag.setStatus(pushStatus);
            tagMapper.updateById(tag);

            // 再删除 Redis
            String redisKey = CACHE_TAG_PRE + tag.getId();
            RedisUtil.del(redisKey);
        }
    }

    /**
     * 根据搜索条件获取标签列表的分页数据。
     *
     * @param searchTagDTO 搜索条件，包含标签名和分页信息。
     * @return 返回包含标签信息的分页对象。
     */
    @Override
    public CommonPageVo<TagDTO> getTagList(SearchTagDTO searchTagDTO) {
        // 构建查询条件，筛选未删除的标签，根据搜索关键字模糊匹配标签名，按更新时间降序排列。
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
                .apply(StringUtils.isNotBlank(searchTagDTO.getTag()),
                        "LOWER(tag_name) LIKE {0}",
                        "%" + searchTagDTO.getTag().toLowerCase() + "%")
                .orderByDesc(Tag::getUpdateTime)
                .last(CommonPageParam.getLimitSql(
                        CommonPageParam.newInstance(searchTagDTO.getPageNumber(), searchTagDTO.getPageSize())));

        // 根据查询条件获取标签列表
        List<Tag> list = tagMapper.selectList(wrapper);

        // 将标签实体转换为DTO列表
        List<TagDTO> tagDTOS = list.stream()
                .map(this::tagToDTO)
                .collect(Collectors.toList());

        // 计算总记录数（不带分页条件），用于分页计算
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getDeleted, CommonDeletedEnum.NO.getCode())
                .apply(StringUtils.isNotBlank(searchTagDTO.getTag()),
                        "LOWER(tag_name) LIKE {0}",
                        "%" + searchTagDTO.getTag().toLowerCase() + "%");

        Long totalCount = tagMapper.selectCount(queryWrapper);

        // 构建并返回标签的分页响应对象
        return CommonPageVo.build(tagDTOS, searchTagDTO.getPageSize(), searchTagDTO.getPageNumber(), totalCount);
    }

    private TagDTO tagToDTO(Tag tag) {
        if ( tag == null ) {
            return null;
        }

        TagDTO tagDTO = new TagDTO();

        tagDTO.setTagId( tag.getId() );
        tagDTO.setTag( tag.getTagName() );
        tagDTO.setStatus( tag.getStatus() );

        return tagDTO;
    }


    /**
     * 根据标签ID获取标签信息。
     * 优先从Redis缓存中获取标签信息，如果缓存中不存在，则从数据库中查询，并将查询结果存入缓存。
     * 这样做的目的是为了提高标签信息的查询效率，减少对数据库的直接访问。
     *
     * @param tagId 标签ID，用于唯一标识一个标签。
     * @return 返回对应的标签DTO对象，如果标签不存在，则返回null。
     */
    @Override
    public TagDTO getTagById(Long tagId) {
        // 构建Redis中的标签缓存键名
        String redisKey = CACHE_TAG_PRE + tagId;

        // 尝试从Redis缓存中获取标签信息
        // 先查询缓存，如果有就直接返回
        String tagInfoStr = RedisUtil.get(redisKey);
        if (tagInfoStr != null && !tagInfoStr.isEmpty()) {
            return JsonUtil.strToObj(tagInfoStr, TagDTO.class);
        }

        // 如果缓存中不存在，则从数据库中查询标签信息
        // 如果未查询到，需要先查询 DB ，再写入缓存
        Tag tag = this.getById(tagId);

        // 如果数据库中也不存在该标签，则返回null
        if (tag == null) {
            return null;
        }

        // 构建标签DTO对象，用于返回给调用方
        TagDTO dto = new TagDTO();
        dto.setTag(tag.getTagName());
        dto.setTagId(tag.getId());
        dto.setStatus(tag.getStatus());

        // 将标签DTO对象转换为字符串，并存入Redis缓存中，设置过期时间
        tagInfoStr = JsonUtil.objToStr(dto);
        RedisUtil.setEx(redisKey, tagInfoStr, CACHE_TAG_EXPRIE_TIME);

        return dto;
    }

}
