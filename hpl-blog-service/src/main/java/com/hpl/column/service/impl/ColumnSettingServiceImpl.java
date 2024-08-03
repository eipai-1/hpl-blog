//package com.hpl.column.service.impl;
//
//import cn.hutool.core.collection.CollUtil;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//
//
//import com.hpl.article.pojo.entity.Article;
//import com.hpl.column.pojo.dto.ColumnArticleDTO;
//import com.hpl.column.pojo.dto.ColumnArticlePostDTO;
//import com.hpl.column.pojo.dto.ColumnDTO;
//import com.hpl.column.pojo.dto.ColumnPostDTO;
//import com.hpl.column.pojo.entity.ColumnArticle;
//import com.hpl.column.pojo.entity.Column;
//import com.hpl.column.mapper.ColumnMapper;
//import com.hpl.article.pojo.dto1.*;
//import com.hpl.article.service.*;
//import com.hpl.column.service.ColumnArticleService;
//import com.hpl.column.service.ColumnService;
//import com.hpl.column.service.ColumnSettingService;
//import com.hpl.enums.StatusEnum;
//import com.hpl.pojo.CommonPageParam;
//import com.hpl.pojo.CommonPageVo;
//import com.hpl.user.pojo.entity.UserInfo;
//import com.hpl.user.service.UserInfoService;
//import com.hpl.util.ExceptionUtil;
//import com.hpl.util.NumUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * 专栏后台接口
// *
// * @author louzai
// * @date 2022-09-19
// */
//@Service
//public class ColumnSettingServiceImpl implements ColumnSettingService {
//
//    @Autowired
//    private ColumnMapper columnMapper;
//
//    @Autowired
//    private ColumnService columnService;
//
//    @Autowired
//    private UserInfoService userInfoService;
//
//    @Autowired
//    private ColumnArticleService columnArticleService;
//
//
//
//    @Autowired
//    private ArticleReadService articleReadService;
//
//    @Autowired
//    private ArticleWriteService articleWriteService;
//
//
//
//
//    @Override
//    public void saveColumn(ColumnPostDTO columnPostDTO) {
//        Column column = this.columnPostToEntity(columnPostDTO);
//        if (NumUtil.eqZero(columnPostDTO.getColumnId())) {
//            columnMapper.insert(column);
//        } else {
//            column.setId(columnPostDTO.getColumnId());
//            columnMapper.updateById(column);
//        }
//    }
//
//    private Column columnPostToEntity(ColumnPostDTO columnPostDTO) {
//        if ( columnPostDTO == null ) {
//            return null;
//        }
//
//        Column column = new Column();
//
//        column.setColumnName( columnPostDTO.getColumn() );
//        column.setAuthorId( columnPostDTO.getAuthor() );
//        column.setIntroduction( columnPostDTO.getIntroduction() );
//        column.setCover( columnPostDTO.getCover() );
//        column.setState( columnPostDTO.getState() );
//        column.setSection( columnPostDTO.getSection() );
//        column.setNums( columnPostDTO.getNums() );
//        column.setType( columnPostDTO.getType() );
//
//        column.setFreeStartTime( new Date(columnPostDTO.getFreeStartTime()) );
//        column.setFreeEndTime( new Date(columnPostDTO.getFreeEndTime()) );
//
//        return column;
//    }
//
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void saveColumnArticle(ColumnArticlePostDTO columnArticlePostDTO) {
//
//        ColumnArticle columnArticle = this.columnArticlePostToEntity(columnArticlePostDTO);
//
//
//
//        if (NumUtil.eqZero(columnArticle.getId())) {
//            // 插入的时候，需要判断是否已经存在
//            ColumnArticle exist = columnArticleService.getOne(Wrappers.<ColumnArticle>lambdaQuery()
//                    .eq(ColumnArticle::getColumnId, columnArticle.getColumnId())
//                    .eq(ColumnArticle::getArticleId, columnArticle.getArticleId()));
//
//            if (exist != null) {
//                throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "请勿重复添加");
//            }
//
//            // section 自增+1
//            Integer maxSection = columnArticleService.getCountByColumnId(columnArticle.getColumnId());
//            columnArticle.setSection(maxSection + 1);
//            columnArticleService.insert(columnArticle);
//        } else {
//            columnArticleService.updateById(columnArticle);
//        }
//
//        // 同时，更新 article 的 shortTitle 短标题
//        if (columnArticlePostDTO.getShortTitle() != null) {
//            Article articleDO = new Article();
//            articleDO.setShortTitle(columnArticlePostDTO.getShortTitle());
//            articleDO.setId(columnArticlePostDTO.getArticleId());
//            articleWriteService.updateById(articleDO);
//        }
//    }
//
//    private ColumnArticle columnArticlePostToEntity(ColumnArticlePostDTO columnArticlePostDTO) {
//        if ( columnArticlePostDTO == null ) {
//            return null;
//        }
//
//        ColumnArticle columnArticle = new ColumnArticle();
//
//        columnArticle.setId( columnArticlePostDTO.getId() );
//        columnArticle.setColumnId( columnArticlePostDTO.getColumnId() );
//        columnArticle.setArticleId( columnArticlePostDTO.getArticleId() );
//
//        return columnArticle;
//    }
//
//    @Override
//    public void deleteColumn(Long columnId) {
//        columnMapper.deleteById(columnId);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteColumnArticle(Long id) {
//        ColumnArticle columnArticle = columnArticleService.getById(id);
//        if (columnArticle != null) {
//            columnArticleService.deleteById(id);
//
//            // 删除的时候，批量更新 section，比如说原来是 1,2,3,4,5,6,7,8,9,10，删除 5，那么 6-10 的 section 都要减 1
//            columnArticleService.update(Wrappers.<ColumnArticle>lambdaUpdate()
//                    .setSql("section = section - 1")
//                    .eq(ColumnArticle::getColumnId, columnArticle.getColumnId())
//                    // section 大于 1
//                    .gt(ColumnArticle::getSection, 1)
//                    .gt(ColumnArticle::getSection, columnArticle.getSection()));
//        }
//    }
//
//    /**
//     * 根据搜索关键字查询简化的列信息列表。
//     *
//     * 本方法通过LambdaQueryWrapper构建查询条件，筛选出列名中包含关键字的列信息，
//     * 并按照列ID降序排列。查询结果将转换为SimpleColumnDTO对象的列表返回。
//     *
//     * @param key 搜索关键字，用于筛选列名中包含该关键字的列信息。
//     * @return 包含简化的列信息的列表。
//     */
//    @Override
//    public List<SimpleColumnDTO> listSimpleColumnBySearchKey(String key) {
//        // 构建查询条件，选择id、columnName和cover属性，列名中包含关键字key
//        LambdaQueryWrapper<Column> wrapper =  Wrappers.lambdaQuery();
//        wrapper.select(Column::getId, Column::getColumnName, Column::getCover)
//                .and(!StringUtils.isEmpty(key),
//                        v -> v.like(Column::getColumnName, key)
//                )
//                .orderByDesc(Column::getId);
//        // 根据构建的查询条件执行查询
//        List<Column> columns = columnMapper.selectList(wrapper);
//
//        // 将查询结果转换为SimpleColumnDTO对象的列表
//        return columns.stream()
//                .map(this::infoToSimpleDto)
//                .collect(Collectors.toList());
//    }
//
//    private SimpleColumnDTO infoToSimpleDto(Column column) {
//        if ( column == null ) {
//            return null;
//        }
//
//        SimpleColumnDTO simpleColumnDTO = new SimpleColumnDTO();
//
//        simpleColumnDTO.setColumnId( column.getId() );
//        simpleColumnDTO.setColumn( column.getColumnName() );
//        simpleColumnDTO.setCover( column.getCover() );
//
//        return simpleColumnDTO;
//    }
//
//    @Override
//    public CommonPageVo<ColumnDTO> getColumnList(SearchColumnDTO searchColumnDTO) {
//
//        LambdaQueryWrapper<Column> query = Wrappers.lambdaQuery();
//        CommonPageParam pageParam = CommonPageParam.newInstance(searchColumnDTO.getPageNumber(), searchColumnDTO.getPageSize());
//
//        // 加上判空条件
//        query.like(StringUtils.isNotBlank(searchColumnDTO.getColumn()), Column::getColumnName, searchColumnDTO.getColumn());
//        query.last(CommonPageParam.getLimitSql(pageParam))
//                .orderByAsc(Column::getSection)
//                .orderByDesc(Column::getUpdateTime);
//
//        List<Column> columnList = columnMapper.selectList(query);
//
//        // 转属性
//        List<ColumnDTO> columnDTOS = columnList.stream()
//                .map(this::infoToDTO)
//                .collect(Collectors.toList());
//
//        // 进行优化，由原来的多次查询用户信息，改为一次查询用户信息
//        // 获取所有需要的用户id
//        // 判断 columnDTOS 是否为空
//        if (CollUtil.isNotEmpty(columnDTOS)) {
//            List<Long> userIds = columnDTOS.stream().map(ColumnDTO::getAuthor).collect(Collectors.toList());
//
//            // 查询所有的用户信息
//            List<UserInfo> users = new ArrayList<>();
//            for(Long userId:userIds){
//                UserInfo user = userInfoService.getByUserId(userId);
//                users.add(user);
//            }
//
//            // 创建一个id到用户信息的映射
//            Map<Long, UserInfo> userMap = users.stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
//
//            // 设置作者信息
//            columnDTOS.forEach(columnDTO -> {
//                UserInfo user = userMap.get(columnDTO.getAuthor());
//                columnDTO.setAuthorName(user.getNickName());
//                columnDTO.setAuthorAvatar(user.getPhoto());
//                columnDTO.setAuthorProfile(user.getProfile());
//            });
//        }
//
//        LambdaQueryWrapper<Column> wrapper = Wrappers.lambdaQuery();
//        wrapper.like(StringUtils.isNotBlank(searchColumnDTO.getColumn()), Column::getColumnName, searchColumnDTO.getColumn());
//        Long totalCount = columnMapper.selectCount(wrapper);
//
//        return CommonPageVo.build(columnDTOS, searchColumnDTO.getPageSize(), searchColumnDTO.getPageNumber(), totalCount);
//    }
//
//    private ColumnDTO infoToDTO(Column column) {
//        if ( column == null ) {
//            return null;
//        }
//
//        ColumnDTO columnDTO = new ColumnDTO();
//
//        columnDTO.setColumnId( column.getId() );
//        columnDTO.setColumn( column.getColumnName() );
//        columnDTO.setAuthor( column.getAuthorId() );
//        columnDTO.setIntroduction( column.getIntroduction() );
//        columnDTO.setCover( column.getCover() );
//        columnDTO.setSection( column.getSection() );
//        columnDTO.setState( column.getState() );
//        columnDTO.setNums( column.getNums() );
//        columnDTO.setType( column.getType() );
//
//        columnDTO.setPublishTime( column.getPublishTime().getTime() );
//        columnDTO.setFreeStartTime( column.getFreeStartTime().getTime() );
//        columnDTO.setFreeEndTime( column.getFreeEndTime().getTime() );
//
//        return columnDTO;
//    }
//
//    @Override
//    public CommonPageVo<ColumnArticleDTO> getColumnArticleList(SearchColumnArticleDTO searchColumnArticleDTO) {
//
//        // 查询
//        List<ColumnArticleDTO> simpleArticleDTOS = columnArticleService.listColumnArticlesDetail(searchColumnArticleDTO, CommonPageParam.newInstance(searchColumnArticleDTO.getPageNumber(), searchColumnArticleDTO.getPageSize()));
//
//
//        int totalCount = columnArticleService.countColumnArticles(searchColumnArticleDTO);
//
//        return CommonPageVo.build(simpleArticleDTOS, searchColumnArticleDTO.getPageSize(), searchColumnArticleDTO.getPageNumber(), totalCount);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void sortColumnArticleApi(SortColumnArticleDTO sortColumnArticleDTO) {
//        // 根据 sortColumnArticleDTO 的两个 ID 调换两篇文章的顺序
//        ColumnArticle activeDO = columnArticleService.getById(sortColumnArticleDTO.getActiveId());
//        ColumnArticle overDO = columnArticleService.getById(sortColumnArticleDTO.getOverId());
//        if (activeDO != null && overDO != null && !activeDO.getId().equals(overDO.getId())) {
//            Integer activeSection = activeDO.getSection();
//            Integer overSection = overDO.getSection();
//            // 假如原始顺序为1、2、3、4
//            //
//            //把 1 拖到 4 后面 2 变 1 3 变 2 4 变 3 1 变 4
//            //把 1 拖到 3 后面 2 变 1 3 变 2 4 不变 1 变 3
//            //把 1 拖到 2 后面 2 变 1 3 不变 4 不变 1 变 2
//            //把 2 拖到 4 后面 1 不变 3 变 2 4 变 3 2 变 4
//            //把 2 拖到 3 后面 1 不变 3 变 2 4 不变 2 变 3
//            //把 3 拖到 4 后面 1 不变 2 不变 4 变 3 3 变 4
//            //把 4 拖到 1 前面 1 变 2 2 变 3 3 变 4
//            //把 4 拖到 2 前面 1 不变 2 变 3 3 变 4  4 变 1
//            //把 4 拖到 3 前面 1 不变 2 不变 3 变 4 4 变 1
//            //把 3 拖到 1 前面 1 变 2 2 变 3 3 变 4 4 变 1
//            //依次类推
//            // 1. 如果 activeSection > overSection，那么 activeSection - 1 到 overSection 的 section 都要 +1
//            // 向上拖动
//            if (activeSection > overSection) {
//                // 当 activeSection 大于 overSection 时，表示文章被向上拖拽。
//                // 需要将 activeSection 到 overSection（不包括 activeSection 本身）之间的所有文章的 section 加 1，
//                // 并将 activeSection 设置为 overSection。
//                columnArticleService.update(Wrappers.<ColumnArticle>lambdaUpdate()
//                        .setSql("section = section + 1") // 将符合条件的记录的 section 字段的值增加 1
//                        .eq(ColumnArticle::getColumnId, overDO.getColumnId()) // 指定要更新记录的 columnId 条件
//                        .ge(ColumnArticle::getSection, overSection) // 指定 section 字段的下限（包含此值）
//                        .lt(ColumnArticle::getSection, activeSection)); // 指定 section 字段的上限
//
//                // 将 activeDO 的 section 设置为 overSection
//                activeDO.setSection(overSection);
//                columnArticleService.updateById(activeDO);
//            } else {
//                // 2. 如果 activeSection < overSection，
//                // 那么 activeSection + 1 到 overSection 的 section 都要 -1
//                // 向下拖动
//                // 需要将 activeSection 到 overSection（包括 overSection）之间的所有文章的 section 减 1
//                columnArticleService.update(Wrappers.<ColumnArticle>lambdaUpdate()
//                        .setSql("section = section - 1") // 将符合条件的记录的 section 字段的值减少 1
//                        .eq(ColumnArticle::getColumnId, overDO.getColumnId()) // 指定要更新记录的 columnId 条件
//                        .gt(ColumnArticle::getSection, activeSection) // 指定 section 字段的下限（不包含此值）
//                        .le(ColumnArticle::getSection, overSection)); // 指定 section 字段的上限（包含此值）
//
//                // 将 activeDO 的 section 设置为 overSection -1
//                activeDO.setSection(overSection);
//                columnArticleService.updateById(activeDO);
//
//            }
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void sortColumnArticleByIDApi(SortColumnArticleByIdDTO sortColumnArticleByIdDTO) {
//        // 获取要重新排序的专栏文章
//        ColumnArticle columnArticleDO = columnArticleService.getById(sortColumnArticleByIdDTO.getId());
//        // 不等于空
//        if (columnArticleDO == null) {
//            throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "教程不存在");
//        }
//        // 如果顺序没变
//        if (sortColumnArticleByIdDTO.getSort().equals(columnArticleDO.getSection())) {
//            return;
//        }
//        // 获取教程可以调整的最大顺序
//        Integer maxSection = columnArticleService.getCountByColumnId(columnArticleDO.getColumnId());
//        // 如果输入的顺序大于最大顺序，提示错误
//        if (sortColumnArticleByIdDTO.getSort() > maxSection) {
//            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "顺序超出范围");
//        }
//        // 查看输入的顺序是否存在
//        ColumnArticle changeColumnArticleDO = columnArticleService.getByColumnIdAndSort(columnArticleDO.getColumnId(), sortColumnArticleByIdDTO.getSort());
//        // 如果存在，交换顺序
//        if (changeColumnArticleDO != null) {
//            // 交换顺序
//            columnArticleService.update(Wrappers.<ColumnArticle>lambdaUpdate()
//                    .set(ColumnArticle::getSection, columnArticleDO.getSection())
//                    .eq(ColumnArticle::getId, changeColumnArticleDO.getId()));
//            columnArticleService.update(Wrappers.<ColumnArticle>lambdaUpdate()
//                    .set(ColumnArticle::getSection, changeColumnArticleDO.getSection())
//                    .eq(ColumnArticle::getId, columnArticleDO.getId()));
//        } else {
//            // 如果不存在，直接修改顺序
//            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "输入的顺序不存在，无法完成交换");
//        }
//    }
//
//
//
//
//
//}
