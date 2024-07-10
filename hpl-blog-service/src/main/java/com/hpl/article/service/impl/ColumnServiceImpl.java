package com.hpl.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hpl.article.dto.*;
import com.hpl.article.entity.ColumnArticle;
import com.hpl.article.entity.ColumnInfo;
import com.hpl.article.enums.ColumnStatusEnum;
import com.hpl.article.mapper.ColumnInfoMapper;
import com.hpl.article.service.ArticleReadService;
import com.hpl.article.service.ColumnArticleService;
import com.hpl.article.service.ColumnService;
import com.hpl.enums.StatusEnum;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserInfoService;
import com.hpl.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YiHui
 * @date 2022/9/14
 */
@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnInfoMapper columnInfoMapper ;

    @Autowired
    private ArticleReadService articleReadService ;

    @Autowired
    private ColumnArticleService columnArticleService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public ColumnArticle getColumnArticleRelation(Long articleId) {
        return columnArticleService.getById(articleId);
    }

    /**
     * 查询专栏列表
     *
     * @param pageParam 分页参数，包含当前页码和每页条数等信息
     * @return 返回带有专栏数据的分页响应对象
     */
    @Override
    public CommonPageListVo<ColumnDTO> listColumn(CommonPageParam pageParam) {
        // 创建查询条件，筛选状态为在线的专栏，并按板块顺序排序
        LambdaQueryWrapper<ColumnInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(ColumnInfo::getState, ColumnStatusEnum.OFFLINE.getCode())
                .last(CommonPageParam.getLimitSql(pageParam))
                .orderByAsc(ColumnInfo::getSection);
        // 根据查询条件获取专栏信息列表
        List<ColumnInfo> columnList = columnInfoMapper.selectList(wrapper);

        // 将专栏信息转换为DTO对象列表
        List<ColumnDTO> result = columnList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // 构建分页响应对象，包含转换后的DTO列表和分页信息
        return CommonPageListVo.newVo(result, pageParam.getPageSize());
    }


    private ColumnDTO toDto(ColumnInfo info) {
        ColumnDTO dto = new ColumnDTO();
        dto.setColumnId(info.getId());
        dto.setColumn(info.getColumnName());
        dto.setCover(info.getCover());
        dto.setIntroduction(info.getIntroduction());
        dto.setState(info.getState());
        dto.setNums(info.getNums());
        dto.setAuthor(info.getAuthorId());
        dto.setSection(info.getSection());
        dto.setPublishTime(info.getPublishTime().getTime());
        dto.setType(info.getType());
        dto.setFreeStartTime(info.getFreeStartTime().getTime());
        dto.setFreeEndTime(info.getFreeEndTime().getTime());
        return dto;
    }

    @Override
    public ColumnDTO queryBasicColumnInfo(Long columnId) {
        // 查找专栏信息
        ColumnInfo column = columnInfoMapper.selectById(columnId);
        if (column == null) {
            throw ExceptionUtil.of(StatusEnum.COLUMN_NOT_EXISTS, columnId);
        }

        return this.toDto(column);
    }

    @Override
    public ColumnDTO queryColumnInfo(Long columnId) {
        return buildColumnInfo(queryBasicColumnInfo(columnId));
    }


    /**
     * 构建专栏详情信息
     *
     * @param dto
     * @return
     */
    private ColumnDTO buildColumnInfo(ColumnDTO dto) {
        // 补齐专栏对应的用户信息
        UserInfo user = userInfoService.getByUserId(dto.getAuthor());
        dto.setAuthorName(user.getNickName());
        dto.setAuthorAvatar(user.getPhoto());
        dto.setAuthorProfile(user.getProfile());

        // 统计计数
        ColumnFootCountDTO countDTO = new ColumnFootCountDTO();
        // 更新文章数
        countDTO.setArticleCount(columnArticleService.getCountByColumnId(dto.getColumnId()));
        // 专栏阅读人数
        countDTO.setReadCount(columnArticleService.getCountReadUserColumn(dto.getColumnId()));
        // 总的章节数
        countDTO.setTotalNums(dto.getNums());
        dto.setCount(countDTO);
        return dto;
    }


    @Override
    public ColumnArticle queryColumnArticle(long columnId, Integer section) {
        ColumnArticle article = columnArticleService.getNthColumnArticle(columnId, section);
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, section);
        }
        return article;
    }

    @Override
    public List<SimpleArticleDTO> queryColumnArticles(long columnId) {
        return columnArticleService.listColumnArticles(columnId);
    }

    @Override
    public Long getTutorialCount() {
        return columnArticleService.getCountByArticleId(null);
    }



}
