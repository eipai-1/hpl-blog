package com.hpl.column.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.column.pojo.dto.ColumnListDTO;
import com.hpl.column.pojo.dto.ColumnPostDTO;
import com.hpl.column.mapper.ColumnInfoMapper;
import com.hpl.article.service.ArticleReadService;
import com.hpl.column.pojo.entity.ColumnInfo;
import com.hpl.column.service.ColumnArticleService;
import com.hpl.column.service.ColumnInfoService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.statistic.pojo.dto.CountAllDTO;
import com.hpl.statistic.service.ReadCountService;
import com.hpl.statistic.service.TraceCountService;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author YiHui
 * @date 2022/9/14
 */
@Service
public class ColumnInfoServiceImpl extends ServiceImpl<ColumnInfoMapper,ColumnInfo> implements ColumnInfoService {

    @Resource
    private ColumnInfoMapper columnInfoMapper;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TraceCountService traceCountService;

    @Resource
    private ReadCountService readCountService;


    @Autowired
    private ColumnArticleService columnArticleService;



    @Override
    public void publishColumn(ColumnPostDTO columnPostDTO){
        ColumnInfo columnInfo = new ColumnInfo();
        BeanUtils.copyProperties(columnPostDTO, columnInfo);

        columnInfoMapper.insert(columnInfo);
    }


    @Override
    public List<ColumnListDTO> listColumns(){

        List<ColumnListDTO> res = new ArrayList<>();

        // 1、处理专栏信息
        // 1.1 查询所有专栏信息并排序
        List<ColumnInfo> columnList = lambdaQuery()
                .eq(ColumnInfo::getDeleted, CommonDeletedEnum.NO.getCode())
                .orderByDesc(ColumnInfo::getSection)
                .list();

        // 1.2 遍历专栏信息
        columnList.forEach(columnInfo -> {

            // 1.3 填充专栏信息
            ColumnListDTO columnListDTO = new ColumnListDTO();
            columnListDTO.setColumnId(columnInfo.getId());
            columnListDTO.setIntroduction(columnInfo.getIntroduction());
            columnListDTO.setColumnName(columnInfo.getColumnName());
            columnListDTO.setCover(columnInfo.getCover());
            columnListDTO.setCreateTime(columnInfo.getCreateTime());


            // 2、处理作者信息
            // 2.1 查询作者信息
            UserInfo userInfo = userInfoService.getByUserId(columnInfo.getAuthorId());
            // 2.2 填充作者信息
            columnListDTO.setAuthorId(userInfo.getId());
            columnListDTO.setAuthorName(userInfo.getNickName());
            columnListDTO.setAuthorAvatar(userInfo.getPhoto());

            // 3、处理统计信息
            // 3.1 查询专栏中的文章id集合
            List<Long> articleIds = columnArticleService.getArticleIds(columnInfo.getId());

            // 3.2 填充文章数量
            columnListDTO.setArticleCount(articleIds.size());

            // 3.3 遍历文章id集合，获取阅读次数总和
            Integer readCountTotal = 0;
            for (Long articleId : articleIds) {
                readCountTotal += readCountService.getArticleReadCount(articleId);
            }
            columnListDTO.setReadCount(readCountTotal);


            // 3.4 遍历文章id集合，获取收藏、点赞、评论次数总和
            Integer collectedCountTotal = 0;
            Integer commentedCountTotal = 0;
            Integer praisedCountTotal = 0;

            for (Long articleId : articleIds) {
                CountAllDTO countAllDTO = traceCountService.getAllCountByArticleId(null,articleId);

                collectedCountTotal += countAllDTO.getCollectionCount();
                commentedCountTotal += countAllDTO.getCommentCount();
                praisedCountTotal += countAllDTO.getPraiseCount();

            }

            columnListDTO.setCollectionCount(collectedCountTotal);
            columnListDTO.setCommentCount(commentedCountTotal);
            columnListDTO.setPraiseCount(praisedCountTotal);

            res.add(columnListDTO);
        });

        return res;
    }


//    /**
//     * 查询专栏列表
//     *
//     * @param pageParam 分页参数，包含当前页码和每页条数等信息
//     * @return 返回带有专栏数据的分页响应对象
//     */
//    @Override
//    public CommonPageListVo<ColumnDTO> listColumn(CommonPageParam pageParam) {
//        // 创建查询条件，筛选状态为在线的专栏，并按板块顺序排序
//        LambdaQueryWrapper<ColumnInfo> wrapper = new LambdaQueryWrapper<>();
//        wrapper.last(CommonPageParam.getLimitSql(pageParam))
//                .orderByAsc(ColumnInfo::getSection);
//        // 根据查询条件获取专栏信息列表
//        List<ColumnInfo> columnList = columnInfoMapper.selectList(wrapper);
//
//        // 将专栏信息转换为DTO对象列表
//        List<ColumnDTO> result = columnList.stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//
//        // 构建分页响应对象，包含转换后的DTO列表和分页信息
//        return CommonPageListVo.newVo(result, pageParam.getPageSize());
//    }


//    @Override
//    public ColumnArticle getColumnArticleRelation(Long articleId) {
//        return columnArticleService.getById(articleId);
//    }
//
//    private ColumnDTO toDto(Column info) {
//        ColumnDTO dto = new ColumnDTO();
//        dto.setColumnId(info.getId());
//        dto.setColumn(info.getColumnName());
//        dto.setCover(info.getCover());
//        dto.setIntroduction(info.getIntroduction());
//        dto.setAuthor(info.getAuthorId());
//        dto.setSection(info.getSection());
//        return dto;
//    }
//
//    @Override
//    public ColumnDTO queryBasicColumnInfo(Long columnId) {
//        // 查找专栏信息
//        Column column = columnMapper.selectById(columnId);
//        if (column == null) {
//            throw ExceptionUtil.of(StatusEnum.COLUMN_NOT_EXISTS, columnId);
//        }
//
//        return this.toDto(column);
//    }
//
//    @Override
//    public ColumnDTO queryColumnInfo(Long columnId) {
//        return buildColumnInfo(queryBasicColumnInfo(columnId));
//    }
//
//
//    /**
//     * 构建专栏详情信息
//     *
//     * @param dto
//     * @return
//     */
//    private ColumnDTO buildColumnInfo(ColumnDTO dto) {
//        // 补齐专栏对应的用户信息
//        UserInfo user = userInfoService.getByUserId(dto.getAuthor());
//        dto.setAuthorName(user.getNickName());
//        dto.setAuthorAvatar(user.getPhoto());
//        dto.setAuthorProfile(user.getProfile());
//
//        // 统计计数
//        ColumnFootCountDTO countDTO = new ColumnFootCountDTO();
//        // 更新文章数
//        countDTO.setArticleCount(columnArticleService.getCountByColumnId(dto.getColumnId()));
//        // 专栏阅读人数
//        countDTO.setReadCount(columnArticleService.getCountReadUserColumn(dto.getColumnId()));
//        // 总的章节数
//        countDTO.setTotalNums(dto.getNums());
//        dto.setCount(countDTO);
//        return dto;
//    }
//
//
//    @Override
//    public ColumnArticle queryColumnArticle(long columnId, Integer section) {
//        ColumnArticle article = columnArticleService.getNthColumnArticle(columnId, section);
//        if (article == null) {
//            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, section);
//        }
//        return article;
//    }
//
//    @Override
//    public List<SimpleArticleDTO> queryColumnArticles(long columnId) {
//        return columnArticleService.listColumnArticles(columnId);
//    }
//
//    @Override
//    public Long getTutorialCount() {
//        return columnArticleService.getCountByArticleId(null);
//    }
//


}
