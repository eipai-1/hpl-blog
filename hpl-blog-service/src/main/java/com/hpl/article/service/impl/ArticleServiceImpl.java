package com.hpl.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.mapper.ArticleMapper;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.enums.OfficalStateEnum;
import com.hpl.article.pojo.enums.PushStatusEnum;
import com.hpl.article.pojo.vo.ArticleListVo;
import com.hpl.article.service.ArticleService;
import com.hpl.article.service.ArticleTagService;
import com.hpl.article.service.CategoryService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.statistic.service.CountService;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserInfoService;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/28 9:00
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    ArticleMapper articleMapper;

    @Resource
    CategoryService categoryService;

    @Resource
    ArticleTagService articleTagService;

    @Resource
    CountService countService;

    @Resource
    UserInfoService userInfoService;

    /**
     * 查询文章列表
     *
     * @param categoryId
     * @param pageParam
     * @return
     */
    @Override
    public CommonPageListVo<ArticleListVo> listArticlesByCategory(Long categoryId, CommonPageParam pageParam) {

        // 处理categoryId参数，无效的分类ID被视为查询所有分类
        if (categoryId != null && categoryId <= 0) {
            // 分类不存在时，表示查所有
            categoryId = null;
        }

        // 创建查询Wrapper，设置文章未删除且状态为在线
        LambdaQueryWrapper<Article> wrapper =  Wrappers.lambdaQuery();
        wrapper.eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Article::getStatus, PushStatusEnum.ONLINE.getCode());

        // 如果是查询首页的置顶文章，且没有指定分类，则只查询官方文章
        // 如果分页中置顶的四条数据，需要加上官方的查询条件
        // 说明是查询官方的文章，非置顶的文章，只限制全部分类
        if (categoryId == null && pageParam.getPageSize() == CommonPageParam.TOP_PAGE_SIZE) {
            wrapper.eq(Article::getOfficalState, OfficalStateEnum.OFFICAL.getCode());
        }

        // 根据categoryId条件，添加分类ID查询条件，如果categoryId为null，则不添加
        Optional.ofNullable(categoryId)
                .ifPresent(cid -> wrapper.eq(Article::getCategoryId, cid));

        // 设置分页和排序，按照置顶状态和创建时间倒序排列
        wrapper.last(CommonPageParam.getLimitSql(pageParam))
                .orderByDesc(Article::getToppingState,  Article::getCreateTime);

        // 执行查询并返回结果列表
        List<Article> records = articleMapper.selectList(wrapper);

        return this.buildArticleListVo(records, pageParam.getPageSize());
    }

    /**
     * 根据文章数据对象列表和分页大小，构建文章列表视图对象。
     * 此方法将内部文章数据对象转换为外部使用的文章DTO（数据传输对象），并根据分页大小准备分页信息。
     *
     * @param records  文章数据对象列表，包含原始文章信息。
     * @param pageSize 每页显示的文章数量，用于计算分页信息。
     * @return 返回包含转换后文章DTO的分页列表视图对象。
     */
//    @Override
    private CommonPageListVo<ArticleListVo> buildArticleListVo(List<Article> records, long pageSize) {

        // 使用流式处理将文章数据对象转换为文章Vo，并收集到列表中
        List<ArticleListVo> result = records.stream()
                .map(this::fillArticleRelatedInfo)
                .collect(Collectors.toList());
        // 根据转换后的文章DTO列表和分页大小，构建并返回分页列表视图对象
        return CommonPageListVo.newVo(result, pageSize);
    }

    /**
     * 根据文章数据对象（Article）填充文章详情传输对象（ArticleDTO）的关联信息。
     * 这包括分类名称、标签列表、阅读统计信息、作者信息等，以丰富文章详情的信息内容。
     *
     * @param article 文章数据对象（Article），包含基础文章信息。
     * @return 填充了关联信息的文章详情传输对象（ArticleDTO）。
     */
    private ArticleListVo fillArticleRelatedInfo(Article article) {

        // 4步走
        // 1、文章内容拼接
        // 2、文章标签内容拼接
        // 3、文章阅读统计信息拼接
        // 4、文章作者信息拼接

        // 1、文章内容拼接
        // 将文章数据对象转换为文章详情传输对象
        ArticleListVo articleListVo =new ArticleListVo();
        articleListVo.setAuthorId(article.getAuthorId());
        articleListVo.setArticleId(article.getId());
        articleListVo.setTitle(article.getTitle());
        articleListVo.setSummary(article.getSummary());
        articleListVo.setUpdateTime(article.getUpdateTime());
        // 分类信息
        articleListVo.setCategoryName(categoryService.getNameById(article.getCategoryId()));

        // 2、文章标签内容拼接
        articleListVo.setTags(articleTagService.getTagsByAId(article.getId()));

        // 3、文章阅读统计信息拼接
        articleListVo.setCountInfo(countService.getArticleStatisticInfo(article.getId()));


        // 4、文章作者信息拼接
        // 查询文章作者的基本信息
        UserInfo author = userInfoService.getByUserId(articleListVo.getAuthorId());
        // 设置作者姓名到文章详情传输对象中
        articleListVo.setAuthorName(author.getNickName());
        // 设置作者头像到文章详情传输对象中
        articleListVo.setAuthorAvatar(author.getPhoto());

        return articleListVo;
    }

//    /**
//     * 返回 优质作者信息的创作文章数
//     */
//    public TopAuthorVO getTopAuthor(){
//        //获取作者的id和文章数
//        articleMapper.selectCount();
//        //查询作者的info信息
//
//        //拼接返回
//    }

}
