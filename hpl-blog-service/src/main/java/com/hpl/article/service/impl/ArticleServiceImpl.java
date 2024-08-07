package com.hpl.article.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.enent.ArticleMsgEvent;
import com.hpl.article.mapper.ArticleDetailMapper;
import com.hpl.article.mapper.ArticleMapper;
import com.hpl.article.pojo.dto.*;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.entity.ArticleDetail;
import com.hpl.article.pojo.entity.ArticleTag;
import com.hpl.article.pojo.enums.*;
import com.hpl.article.pojo.vo.ArticleListVo;
import com.hpl.article.service.ArticleService;
import com.hpl.article.service.ArticleTagService;
import com.hpl.article.service.CategoryService;
import com.hpl.column.pojo.dto.ColumnDirectoryDTO;
import com.hpl.enums.StatusEnum;
import com.hpl.media.service.ImageMdService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.statistic.pojo.entity.ReadCount;
import com.hpl.statistic.service.ReadCountService;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserInfoService;
import com.hpl.user.service.UserRelationService;
import com.hpl.util.ExceptionUtil;
import com.hpl.util.IdUtil;
import com.hpl.util.NumUtil;
import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/28 9:00
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final Integer TOP_SIZE = 8 ;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private ArticleDetailMapper articleDetailMapper;

    @Resource
    private CategoryService categoryService;

    @Resource
    private ArticleTagService articleTagService;

    @Resource
    private ReadCountService readCountService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserRelationService userRelationService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ImageMdService imageMdService;


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
                .eq(Article::getStatus, PublishStatusEnum.PUBLISHED.getCode());

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
        articleListVo.setCountInfo(readCountService.getArticleStatisticInfo(article.getId()));


        // 4、文章作者信息拼接
        // 查询文章作者的基本信息
        UserInfo author = userInfoService.getByUserId(articleListVo.getAuthorId());
        // 设置作者姓名到文章详情传输对象中
        articleListVo.setAuthorName(author.getNickName());
        // 设置作者头像到文章详情传输对象中
        articleListVo.setAuthorAvatar(author.getPhoto());

        return articleListVo;
    }

    /**
     * 返回 优质作者信息
     */
    @Override
    public List<TopAuthorDTO> getTopFourAuthor(Long categoryId){

        List<TopAuthorDTO> res = new ArrayList<>();

        //获取前四位作者的id和文章数
        List<SimpleAuthorCountDTO> simpleAuthorCountsDTO=articleMapper.getTopFourAuthor(categoryId);

        for(SimpleAuthorCountDTO simpleAuthorCountDTO :simpleAuthorCountsDTO){
            // 查询作者的info信息
            UserInfo userInfo = userInfoService.getByUserId(simpleAuthorCountDTO.getAuthorId());

            // 查user_relation 粉丝数量
            Long fansCount = userRelationService.queryUserFansCount(simpleAuthorCountDTO.getAuthorId());

            //todo
            // 我的关注情况 注意处理 用户没登入的情况

            Boolean isFollow = false;
            //todo 如果用户登入了 替换false 和 固定 1L
            if(false){
                isFollow = userRelationService.isFollow(simpleAuthorCountDTO.getAuthorId(),1L);
            }



            //拼接返回
            TopAuthorDTO topAuthorDTO = new TopAuthorDTO();
            topAuthorDTO.setAuthorId(simpleAuthorCountDTO.getAuthorId());
            topAuthorDTO.setAuthorName(userInfo.getNickName());
            topAuthorDTO.setAuthorAvatar(userInfo.getPhoto());
            topAuthorDTO.setAuthorProfile(userInfo.getProfile());
            topAuthorDTO.setCreateTime(userInfo.getCreateTime());

            topAuthorDTO.setArticleCount(simpleAuthorCountDTO.getArticleCount());

            topAuthorDTO.setFansCount(fansCount);
            topAuthorDTO.setIsFollow(isFollow);



            res.add(topAuthorDTO);
        }

        return res;
    }


    /**
     * 获取文章排行
     * @return
     */
    @Override
    public List<TopArticleDTO> getTopEight(){

        //先查redis是否存在
        //todo 配置redis后处理
//        if(redisTemplate.hasKey("topEight")){
//            return (List<TopAuthorDTO>) redisTemplate.opsForValue().get("topEight");
//        }


        List<ReadCount> readCounts =readCountService.getTopCountByCategoryId();

        List<TopArticleDTO> res=new ArrayList<>();

        //循环遍历top 拼接信息
        for(int i = 0; i <TOP_SIZE;i++){
            ReadCount readCount = readCounts.get(i);

            // 查article
            Article article = getById(readCount.getDocumentId());

            // 拼接信息
            TopArticleDTO topArticleDTO = new TopArticleDTO();
            topArticleDTO.setArticleId(article.getId());

            //只取前8个字符
            if(article.getTitle().length()>10){
                topArticleDTO.setTitle(article.getTitle().substring(0,10)+"...");
            }else{
                topArticleDTO.setTitle(article.getTitle());
            }



            topArticleDTO.setCnt(readCount.getCnt());

            res.add(topArticleDTO);
        }

        //todo 添加至缓存

        return res;
    }

    /**
     * 根据文章id获取作者id
     * @param articleId
     * @return
     */
    @Override
    public Long getAuthorIdById(Long articleId){
        return lambdaQuery()
                .eq(Article::getId, articleId)
                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .one()
                .getAuthorId();

    }

    /**
     * 获取文章的id，短标题和更新时间，形成目录
     * @param articleId
     * @return
     */
    @Override
    public ColumnDirectoryDTO getDirectoryById(Long articleId) {
        // 检查 articleId 是否合法
        if (articleId == null || articleId <= 0) {
            throw new IllegalArgumentException("Article ID must be positive.");
        }

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Article::getId, Article::getShortTitle, Article::getUpdateTime)
                .eq(Article::getId, articleId)
                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode());

        Article article;
        try {
            article = articleMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            // 异常处理，如记录日志，并重新抛出或转换为业务异常
            // 例如：logger.error("Error querying article by ID: " + articleId, e);
            throw new RuntimeException("Failed to query article by ID: " + articleId, e);
        }

        if (article == null) {
            // 处理找不到文章的情况，可以抛出自定义异常或返回null，此处选择抛出异常

            //todo
//            throw new CommonException.("Article with ID " + articleId + " not found.");
        }

        return ColumnDirectoryDTO.builder()
                .articleId(article.getId())
                .shoutTitle(article.getShortTitle())
                .updateTime(article.getUpdateTime())
                .build();
    }



    /**
     * 保存文章，当articleId存在时，表示更新记录； 不存在时，表示插入
     *
     * @param articlePostDTO
     * @return
     */
    @Override
    public Long saveOrUpdate(ArticlePostDTO articlePostDTO, Long authorId) {

        Article article = new Article();
        // 设置作者ID
        article.setAuthorId(authorId);
        article.setId(articlePostDTO.getArticleId());
        article.setTitle(articlePostDTO.getTitle());
        article.setShortTitle(articlePostDTO.getShortTitle());
        article.setCategoryId(articlePostDTO.getCategoryId());
        article.setSummary(articlePostDTO.getSummary());
        article.setStatus(articlePostDTO.getStatus());

        //todo
//        article.setSource(articlePostDTO.getSource());
//        article.setSourceUrl(articlePostDTO.getSourceUrl());


        // 处理文章图片
        String content = imageMdService.mdImgReplace(articlePostDTO.getContent());

        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                Long articleId;
                if (NumUtil.eqZero(articlePostDTO.getArticleId())) {
                    articleId = insertArticle(article, content, articlePostDTO.getTagIds());
                    log.info("文章发布成功! title={}", articlePostDTO.getTitle());
                } else {
                    articleId = updateArticle(article, content, articlePostDTO.getTagIds());
                    log.info("文章更新成功！ title={}", article.getTitle());
                }

                //todo
//                if (articlePostDTO.getColumnId() != null) {
//                    // 更新文章对应的专栏信息
//                    //todo
////                    columnSettingService.saveColumnArticle(articleId, articlePostDTO.getColumnId());
//                }
                return articleId;
            }
        });
    }

    /**
     * 新建文章
     *
     * @param article
     * @param content
     * @param tags
     * @return
     */
    private Long insertArticle(Article article, String content, Set<Long> tags) {
        // article + article_detail + tag  三张表的数据变更


        // 1. 保存文章
        // 使用分布式id生成文章主键
        Long articleId = IdUtil.genId();
        article.setId(articleId);
        articleMapper.insert(article);


        // 2. 保存文章内容
        ArticleDetail detail = new ArticleDetail();
        detail.setArticleId(articleId);
        detail.setContent(content);
        detail.setVersion(1L);
        articleDetailMapper.insert(detail);


        // 3. 保存文章标签
        tags.forEach(tagId -> {
            ArticleTag tag = new ArticleTag();
            tag.setTagId(tagId);
            tag.setArticleId(articleId);
            tag.setDeleted(CommonDeletedEnum.NO.getCode());
            articleTagService.save(tag);
        });



        //todo
//        // 发布文章，阅读计数+1
//        userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId, article.getAuthorId(), article.getAuthorId(), OperateTypeEnum.READ);
//
//        // todo 事件发布这里可以进行优化，一次发送多个事件？ 或者借助bit知识点来表示多种事件状态
//        // 发布文章创建事件
//        SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.CREATE, article));
//        // 文章直接上线时，发布上线事件
//        SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.ONLINE, article));

        return articleId;
    }

    /**
     * 更新文章
     *
     * @param article
     * @param content
     * @param tags
     * @return
     */
    private Long updateArticle(Article article, String content, Set<Long> tags) {

        // 1、更新article表
        articleMapper.updateById(article);

        // 2、更新article_detail表
        // 更新内容 和 版本号

        LambdaQueryWrapper<ArticleDetail> contentQuery = Wrappers.lambdaQuery();
        contentQuery.eq(ArticleDetail::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(ArticleDetail::getArticleId, article.getId())
                .orderByDesc(ArticleDetail::getVersion);
        ArticleDetail latest = articleDetailMapper.selectList(contentQuery).get(0);

        // 如何文章是已发布，则版本号+1
        if(article.getStatus()==PublishStatusEnum.PUBLISHED.getCode()){
            latest.setVersion(latest.getVersion() + 1);
        }

        latest.setContent(content);
        articleDetailMapper.updateById(latest);


        // 3、标签更新
        if (tags != null && tags.size() > 0) {
            LambdaQueryWrapper<ArticleTag> query = Wrappers.lambdaQuery();
            query.eq(ArticleTag::getArticleId, article.getId())
                    .eq(ArticleTag::getDeleted, CommonDeletedEnum.NO.getCode());
            List<ArticleTag> dbTags = articleTagService.listObjs(query);

            // 在旧的里面，不在新的里面的标签，设置为删除
            List<Long> toDeleted = new ArrayList<>();
            dbTags.forEach(tag -> {
                if (!tags.contains(tag.getTagId())) {
                    toDeleted.add(tag.getId());
                } else {
                    // 移除已经存在的记录
                    tags.remove(tag.getTagId());
                }
            });
            if (!toDeleted.isEmpty()) {
                articleTagService.removeBatchByIds(toDeleted);
            }

            if (!tags.isEmpty()) {
                List<ArticleTag> insertList = new ArrayList<>(tags.size());
                tags.forEach(s -> {
                    ArticleTag tag = new ArticleTag();
                    tag.setTagId(s);
                    tag.setArticleId(article.getId());
                    tag.setDeleted(CommonDeletedEnum.NO.getCode());
                    articleTagService.save(tag);
                });

            }
        }

        return article.getId();
    }


    /**
     * 删除文章
     *
     * @param articleId
     */
    @Override
    public void deleteArticle(Long articleId, Long loginUserId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getId, articleId)
                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode());

        Article article = articleMapper.selectOne(wrapper);

        if (article != null && !Objects.equals(article.getAuthorId(), loginUserId)) {
            // 没有权限
            throw ExceptionUtil.of(StatusEnum.FORBID_ERROR_MIXED, "请确认文章是否属于您!");
        }

        if (article != null && article.getDeleted() != CommonDeletedEnum.YES.getCode()) {
            article.setDeleted(CommonDeletedEnum.YES.getCode());
            articleMapper.updateById(article);

            // 发布文章删除事件
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.DELETE, article));
        }
    }

    @Override
    public List<MyArticleListDTO> listMyArticles(SearchMyArticleDTO searchMyArticleDTO, Long userId){

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        // todo 处理搜索条件
//        if(searchMyselfDTO!=null){
//
//        }

        wrapper.eq(Article::getAuthorId, userId)
                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .orderByDesc(Article::getCategoryId,Article::getCreateTime);

        //查询文章列表
        List<Article> articles = articleMapper.selectList(wrapper);

        List<MyArticleListDTO> res = new ArrayList<>();

        articles.forEach(article -> {
            MyArticleListDTO dto = new MyArticleListDTO();
            dto.setArticleId(article.getId());
            dto.setAuthorId(article.getAuthorId());

            dto.setTitle(article.getTitle());
            dto.setShortTitle(article.getShortTitle());
            dto.setSummary(article.getSummary());
            dto.setPicture(article.getPicture());

            dto.setCategory(categoryService.getNameById(article.getCategoryId()));

            dto.setStatus(article.getStatus());
            dto.setCreateTime(article.getCreateTime());
            dto.setUpdateTime(article.getUpdateTime());

            res.add(dto);
        });

        return res;
    }

}
