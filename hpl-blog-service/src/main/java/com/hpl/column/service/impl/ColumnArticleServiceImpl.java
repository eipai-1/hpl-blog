package com.hpl.column.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hpl.column.pojo.dto.ColumnArticleDTO;
import com.hpl.article.pojo.dto1.SearchColumnArticleDTO;
import com.hpl.article.pojo.dto1.SimpleArticleDTO;
import com.hpl.column.pojo.entity.ColumnArticle;
import com.hpl.column.mapper.ColumnArticleMapper;
import com.hpl.column.service.ColumnArticleService;
import com.hpl.pojo.CommonPageParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author : rbe
 * @date : 2024/7/6 11:29
 */
@Service
public class ColumnArticleServiceImpl implements ColumnArticleService {

    @Resource
    private ColumnArticleMapper columnArticleMapper;

    @Override
    public List<Long> getArticleIds(Long columnId){
        // 1.查询该专栏下的所有文章id
        return columnArticleMapper.getArticleIds(columnId);
    }

    @Override
    public ColumnArticle getById(Long articleId){
        LambdaQueryWrapper<ColumnArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ColumnArticle::getId, articleId);
        return columnArticleMapper.selectOne(wrapper);
    }

    /**
     * 根据文章id，查询该文章所属专栏的文章数量
     *
     * @param articleId
     * @return
     */
    @Override
    public Long getCountByArticleId(Long articleId){
        LambdaQueryWrapper<ColumnArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(articleId!=null,ColumnArticle::getArticleId, articleId);
        return columnArticleMapper.selectCount(wrapper);
    }

    /**
     * 返回专栏最大更新章节数
     *
     * @param columnId
     * @return 专栏内无文章时，返回0；否则返回当前最大的章节数
     */
    @Override
    public int getCountByColumnId(Long columnId) {
        return columnArticleMapper.getCountByColumnId(columnId);
    }

    /**
     * 根据文章id，查询再所属的专栏信息
     * fixme: 如果一篇文章，在多个专栏内，就会有问题
     *
     * @param articleId
     * @return
     */
    @Override
    public ColumnArticle getByArticleId(Long articleId) {

        LambdaQueryWrapper<ColumnArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ColumnArticle::getArticleId, articleId);

        List<ColumnArticle> list = columnArticleMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public ColumnArticle getByColumnIdAndSort(Long columnId, Integer sort) {

        LambdaQueryWrapper<ColumnArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ColumnArticle::getColumnId, columnId)
                .eq(ColumnArticle::getSection, sort);

        return columnArticleMapper.selectOne(wrapper);
    }


    /**
     * 统计专栏的阅读人数
     *
     * @param columnId
     * @return
     */
    @Override
    public Integer getCountReadUserColumn(Long columnId) {
        return columnArticleMapper.countColumnReadUserNums(columnId).intValue();
    }

    /**
     * 获取专栏中的第N篇文章
     * @param columnId
     * @param section
     * @return
     */
    @Override
    public ColumnArticle getNthColumnArticle(long columnId, Integer section){
        return columnArticleMapper.getColumnArticle(columnId, section);
    }

    @Override
    public List<SimpleArticleDTO> listColumnArticles(Long columnId) {
        return columnArticleMapper.listColumnArticles(columnId);
    }

    /**
     * 将文章保存到对应的专栏中
     *
     * @param articleId
     * @param columnId
     */
    @Override
    public void saveColumnArticle(Long articleId, Long columnId) {
        // 转换参数
        // 插入的时候，需要判断是否已经存在
        ColumnArticle exist = this.getById(articleId);

        if (exist != null) {
            if (!Objects.equals(columnId, exist.getColumnId())) {
                // 更新
                exist.setColumnId(columnId);
                columnArticleMapper.updateById(exist);
            }
        } else {
            // 将文章保存到专栏中，章节序号+1
            ColumnArticle columnArticle = new ColumnArticle();
            columnArticle.setColumnId(columnId);
            columnArticle.setArticleId(articleId);
            // section 自增+1
            Integer maxSection = this.getCountByColumnId(columnId);
            columnArticle.setSection(maxSection + 1);
            columnArticleMapper.insert(columnArticle);
        }
    }

    @Override
    public ColumnArticle getOne(LambdaQueryWrapper<ColumnArticle> wrapper){
        return columnArticleMapper.selectOne(wrapper);
    }

    @Override
    public void insert(ColumnArticle columnArticle){
        columnArticleMapper.insert(columnArticle);
    }

    @Override
    public void updateById(ColumnArticle columnArticle){
        columnArticleMapper.updateById(columnArticle);
    }

    @Override
    public void deleteById(Long id){
        columnArticleMapper.deleteById(id);
    }

    @Override
    public void update(LambdaUpdateWrapper<ColumnArticle> wrapper){
        columnArticleMapper.update(wrapper);
    }

    /**
     * 根据教程ID查询文章信息列表
     * @return
     */
    @Override
    public List<ColumnArticleDTO> listColumnArticlesDetail(SearchColumnArticleDTO params,
                                                           CommonPageParam pageParam) {
        return columnArticleMapper.listColumnArticlesByColumnIdArticleName(params.getColumnId(),
                params.getArticleTitle(),
                pageParam);
    }

    @Override
    public Integer countColumnArticles(SearchColumnArticleDTO searchColumnArticleDTO) {
        return columnArticleMapper.countColumnArticlesByColumnIdArticleName(searchColumnArticleDTO.getColumnId(),
                searchColumnArticleDTO.getArticleTitle()).intValue();
    }
}
