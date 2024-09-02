package com.hpl.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.article.pojo.dto1.CategoryDTO;
import com.hpl.article.pojo.dto1.SimpleArticleDTO;
import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.mapper.ArticleDetailMapper;
import com.hpl.article.mapper.ArticleMapper;
import com.hpl.article.mapper.ArticleTagMapper;
import com.hpl.article.pojo.entity.*;
import com.hpl.article.pojo.enums.*;
import com.hpl.article.service.ArticleReadService;
import com.hpl.article.service.ArticleTagService;
import com.hpl.article.service.oldCategoryService;
import com.hpl.article.service.TagService;
import com.hpl.exception.StatusEnum;
import com.hpl.redis.RedisClient;
import com.hpl.user.context.ReqInfoContext;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageVo;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserFootService;
import com.hpl.user.service.UserInfoService;
import com.hpl.exception.ExceptionUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文章查询相关服务类
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service
public class ArticleReadServiceImpl implements ArticleReadService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private ArticleDetailMapper articleDetailMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private oldCategoryService oldCategoryService;
    /**
     * 在一个项目中，UserFootService 就是内部服务调用
     * 拆微服务时，这个会作为远程服务访问
     */
    @Autowired
    private UserFootService userFootService;

    @Autowired
    private UserInfoService userInfoService;

    @Resource
    private ArticleTagService articleTagService;

    @Resource
    private RedisClient redisClient;



    // 是否开启ES
//    @Value("${elasticsearch.open}")
//    private Boolean openES;

//    @Autowired
//    private RestHighLevelClient restHighLevelClient;

    @Override
    public Article getById(Long articleId) {
        Article article = redisClient.get("article:" + articleId, Article.class);
        if (article != null) {
            return article;
        }

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, articleId)
                .eq(Article::getDeleted,0);
        article = articleMapper.selectOne(queryWrapper);

        redisClient.set("article:" + articleId, article, 60 * 60 * 24L, TimeUnit.SECONDS);


        return article;
    }

    @Override
    public String pickSummary(String content) {

        final Integer MAX_SUMMARY_CHECK_TXT_LEN = 2000;
        final Integer SUMMARY_LEN = 256;
        Pattern LINK_IMG_PATTERN = Pattern.compile("!?\\[(.*?)\\]\\((.*?)\\)");
        Pattern CONTENT_PATTERN = Pattern.compile("[0-9a-zA-Z\u4e00-\u9fa5:;\"'<>,.?/·~！：；“”‘’《》，。？、（）]");

        Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");


        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }

        // 首先移除所有的图片，链接
        content = content.substring(0, Math.min(content.length(), MAX_SUMMARY_CHECK_TXT_LEN)).trim();
        // 移除md的图片、超链
        content = content.replaceAll(LINK_IMG_PATTERN.pattern(), "");
        // 移除html标签
        content = HTML_TAG_PATTERN.matcher(content).replaceAll("");

        // 匹配对应字符
        StringBuilder result = new StringBuilder();
        Matcher matcher = CONTENT_PATTERN.matcher(content);
        while (matcher.find()) {
            result.append(content, matcher.start(), matcher.end());
            if (result.length() >= SUMMARY_LEN) {
                return result.substring(0, SUMMARY_LEN).trim();
            }
        }
        return result.toString().trim();
    }

    /**
     * 已处理
     * 根据文章ID查询关联的标签信息。
     *
     * @param articleId 文章的唯一标识ID。
     * @return 返回包含标签信息的CommonPageVo对象，其中标签信息以TagDTO形式呈现。
     *         CommonPageVo封装了分页信息和数据列表，这里只用到了数据列表部分。
     */
    private List<TagDTO> getTagsByAId(Long articleId) {
        // 初始化用于存储标签DTO的列表
        List<TagDTO> tagsDTO = new ArrayList<>();

        // 构建查询条件，查询与文章ID匹配且未被删除的文章标签信息
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, articleId)
                .eq(ArticleTag::getDeleted,0);
        // 根据查询条件，获取第一篇匹配的文章标签信息
        List<ArticleTag> tags = articleTagMapper.selectList(queryWrapper);

        // 如果找到了文章标签信息，则进一步查询对应的标签详情
        if(tags!=null){
            tags.forEach(t -> {
                // 根据标签ID，获取标签列表
                TagDTO tagDTO = tagService.getById(t.getTagId());


                tagsDTO.add(tagDTO);
            });
        }

        return tagsDTO;
    }

    /**
     * 已处理
     * @param articleId
     * @return
     */
    @Override
    public CommonPageVo<TagDTO> listTagsById(Long articleId) {
        List<TagDTO> tagDTOS=this.getTagsByAId(articleId);
        // 根据tagDTOS列表内容，构建并返回CommonPageVo对象，用于分页展示标签信息
        return CommonPageVo.build(tagDTOS, 1, 10, tagDTOS.size());
    }

    @Override
    public ArticleDTO getArticleInfoById(Long articleId) {

        // 查询文章记录
        Article article = this.getById(articleId);
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }

        ArticleDTO articleDTO=new ArticleDTO();
        BeanUtils.copyProperties(article,articleDTO);
        articleDTO.setArticleId(articleId);
        articleDTO.setSourceType(SourceTypeEnum.formCode(article.getSourceType()).getDesc());
        //todo 1
//        articleDTO.setCategory(new CategoryDTO((article.getCategoryId()),null));

        // 查询文章正文
        articleDTO.setContent(this.getArticleDetailById(articleId).getContent());

        // 更新分类相关信息
        CategoryDTO category = articleDTO.getCategory();
        //todo 1
//        category.setCategory(oldCategoryService.getNameById(category.getCategoryId()));

        // 更新标签信息
        articleDTO.setTags(articleTagService.getTagsByAId(articleId));
        return articleDTO;
    }



    private ArticleDetail getArticleDetailById(long articleId) {

        ArticleDetail articleDetail = redisClient.get("articleDetail:" + articleId, ArticleDetail.class);
        if (articleDetail != null) {
            return articleDetail;
        }

        // 查询文章内容
        LambdaQueryWrapper<ArticleDetail> contentQuery = Wrappers.lambdaQuery();
        contentQuery.eq(ArticleDetail::getDeleted, 0)
                .eq(ArticleDetail::getArticleId, articleId)
                .orderByDesc(ArticleDetail::getVersion);
        articleDetail = articleDetailMapper.selectOne(contentQuery);

        if (articleDetail == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }
        redisClient.set("articleDetail:" + articleId, articleDetail, 60 * 60 * 24L, TimeUnit.SECONDS);

        return articleDetail;
    }




//    /**
//     * 查询文章所有的关联信息，正文，分类，标签，阅读计数，当前登录用户是否点赞、评论过
//     *
//     * @param articleId
//     * @param readUser
//     * @return
//     */
//    @Override
//    public ArticleDTO getFullArticleInfo(Long articleId, Long readUser) {
//        ArticleDTO articleDTO = getArticleInfoById(articleId);
//
//        // 文章阅读计数+1
//        readCountService.incrArticleReadCount(articleDTO.getAuthorId(), articleId);
//
//        // 文章的操作标记
//        if (readUser != null) {
//            // 更新用于足迹，并判断是否点赞、评论、收藏
//            UserFoot foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId,
//                    articleDTO.getAuthorId(), readUser, OperateTypeEnum.READ);
//            articleDTO.setPraised(Objects.equals(foot.getPraiseState(), PraiseStateEnum.PRAISE.getCode()));
//            articleDTO.setCommented(Objects.equals(foot.getCommentState(), CommentStateEnum.COMMENT.getCode()));
//            articleDTO.setCollected(Objects.equals(foot.getCollectionState(), CollectionStateEnum.COLLECTION.getCode()));
//        } else {
//            // 未登录，全部设置为未处理
//            articleDTO.setPraised(false);
//            articleDTO.setCommented(false);
//            articleDTO.setCollected(false);
//        }
//
//        // 更新文章统计计数
////        articleDTO.setCount(readCountService.getArticleStatisticInfo(articleId));
//
//        // 设置文章的点赞列表
//        articleDTO.setPraisedUsers(userFootService.getArticlePraisedUsers(articleId));
//        return articleDTO;
//    }


    /**
     * 已处理
     * 查询文章列表
     *
     * @param categoryId
     * @param pageParam
     * @return
     */
    @Override
    public CommonPageListVo<ArticleDTO> listArticlesByCategory(Long categoryId, CommonPageParam pageParam) {

        // 处理categoryId参数，无效的分类ID被视为查询所有分类
        if (categoryId != null && categoryId <= 0) {
            // 分类不存在时，表示查所有
            categoryId = null;
        }

        // 创建查询Wrapper，设置文章未删除且状态为在线
        LambdaQueryWrapper<Article> wrapper =  Wrappers.lambdaQuery();
        wrapper.eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Article::getStatus, PublishStatusEnum.PUBLISHED.getCode());


        // 根据categoryId条件，添加分类ID查询条件，如果categoryId为null，则不添加
        Optional.ofNullable(categoryId)
                .ifPresent(cid -> wrapper.eq(Article::getCategoryId, cid));

        // 设置分页和排序，按照置顶状态和创建时间倒序排列
        wrapper.last(CommonPageParam.getLimitSql(pageParam))
                .orderByDesc(Article::getCreateTime);

        // 执行查询并返回结果列表
        List<Article> records = articleMapper.selectList(wrapper);

        return this.buildArticleListVo(records, pageParam.getPageSize());
    }

    /**
     * 已处理
     * 根据文章数据对象列表和分页大小，构建文章列表视图对象。
     * 此方法将内部文章数据对象转换为外部使用的文章DTO（数据传输对象），并根据分页大小准备分页信息。
     *
     * @param records  文章数据对象列表，包含原始文章信息。
     * @param pageSize 每页显示的文章数量，用于计算分页信息。
     * @return 返回包含转换后文章DTO的分页列表视图对象。
     */
    @Override
    public CommonPageListVo<ArticleDTO> buildArticleListVo(List<Article> records, long pageSize) {

        // 使用流式处理将文章数据对象转换为文章DTO，并收集到列表中
        List<ArticleDTO> result = records.stream()
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
    private ArticleDTO fillArticleRelatedInfo(Article article) {

        // 将文章数据对象转换为文章详情传输对象
        ArticleDTO articleDTO=new ArticleDTO();
        BeanUtils.copyProperties(article,articleDTO);
        articleDTO.setArticleId(article.getId());
//        articleDTO.setCreateTime(article.getCreateTime().getTime());
//        articleDTO.setLastUpdateTime(article.getUpdateTime().getTime());
        articleDTO.setSourceType(SourceTypeEnum.formCode(article.getSourceType()).getDesc());

        //todo 不用传detail吧 展示的话用不到，点进去才要
//        if (showReviewContent(article)) {
//            ArticleDetail detail = this.getArticleDetailById(article.getId());
//            articleDTO.setContent(detail.getContent());
//        } else {
//            // 对于审核中的文章，只有作者本人才能看到原文
//            articleDTO.setContent("### 文章审核中，请稍后再看");
//        }

        // 设置类目id
        //todo 1
//        articleDTO.setCategory(new CategoryDTO(article.getCategoryId(), null));


        // 分类信息
        //todo 1
//        articleDTO.getCategory().setCategory(oldCategoryService.getNameById(article.getCategoryId()));

        // 标签列表
        articleDTO.setTags(this.getTagsByAId(article.getId()));

        // 阅读计数统计
//        articleDTO.setCount(readCountService.getArticleStatisticInfo(article.getId()));

        // 查询文章作者的基本信息
        UserInfo author = userInfoService.getByUserId(articleDTO.getAuthorId());
        // 设置作者姓名到文章详情传输对象中
        articleDTO.setAuthorName(author.getNickName());
        // 设置作者头像到文章详情传输对象中
        articleDTO.setAuthorAvatar(author.getPhoto());

        return articleDTO;
    }

    /**
     * 查询置顶的文章列表
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<ArticleDTO> getTopArticlesByCategoryId(Long categoryId) {
        CommonPageParam pageParam = CommonPageParam.newInstance(CommonPageParam.DEFAULT_PAGE_NUM, CommonPageParam.TOP_PAGE_SIZE);

        // 处理categoryId参数，无效的分类ID被视为查询所有分类
        if (categoryId != null && categoryId <= 0) {
            // 分类不存在时，表示查所有
            categoryId = null;
        }

        // 创建查询Wrapper，设置文章未删除且状态为在线
        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Article::getStatus, PublishStatusEnum.PUBLISHED.getCode());


        // 根据categoryId条件，添加分类ID查询条件，如果categoryId为null，则不添加
        Optional.ofNullable(categoryId)
                .ifPresent(cid -> wrapper.eq(Article::getCategoryId, cid));

        // 设置分页和排序，按照置顶状态和创建时间倒序排列
        wrapper.last(CommonPageParam.getLimitSql(pageParam))
                .orderByDesc(Article::getCreateTime);

        // 执行查询并返回结果列表
        List<Article> articles = articleMapper.selectList(wrapper);
        return articles.stream()
                .map(this::fillArticleRelatedInfo)
                .collect(Collectors.toList());
    }

    @Override
    public Long getCountByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Article> warpper = Wrappers.lambdaQuery();
        warpper.eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Article::getStatus, PublishStatusEnum.PUBLISHED.getCode())
                .eq(Article::getCategoryId, categoryId);
        return articleMapper.selectCount(warpper);
    }

    /**
     * 查询文章数量和分类。
     * 通过查询文章表中被标记为未删除且状态为在线的文章，按分类统计文章数量。
     * 返回每个分类及其对应的文章数量。
     *
     * @return Map<Long, Long> 分类ID到文章数量的映射。
     */
    @Override
    public Map<Long, Long> queryArticleCountsAndCategory() {
        // 初始化查询条件，查询所有未删除且状态为在线的文章
        QueryWrapper<Article> query = Wrappers.query();
        query.select("category_id, count(*) as cnt")
                .eq("deleted", CommonDeletedEnum.NO.getCode())
                .eq("status", PublishStatusEnum.PUBLISHED.getCode())
                .groupBy("category_id");

        // 执行查询，获取结果列表，每个元素包含分类ID和该分类下的文章数量
        List<Map<String, Object>> mapList = articleMapper.selectMaps(query);

        // 初始化结果映射，预计大小为查询结果的数量，以提高性能
        Map<Long, Long> result = Maps.newHashMapWithExpectedSize(mapList.size());
        // 遍历查询结果，将分类ID和文章数量添加到结果映射中
        for (Map<String, Object> mp : mapList) {
            Long cnt = (Long) mp.get("cnt");
            // 忽略文章数量为0的分类，避免在结果中出现空分类
            if (cnt != null && cnt > 0) {
                result.put((Long) mp.get("category_id"), cnt);
            }
        }
        return result;
    }


//    @Override
//    public CommonPageListVo<ArticleDTO> listArticlesByTag(Long tagId, CommonPageParam pageParam) {
//
////        List<Article> records = articleMapper.listRelatedArticlesOrderByReadCount(null, Arrays.asList(tagId), page);
//
//        List<Article> records = new ArrayList<>();
//
//        // 根据类别ID和标签ID列表查询文章的阅读量信息 todo
//        List<ReadCount> list = articleMapper.listArticleByCategoryAndTags(null, Collections.singletonList(tagId), pageParam);
//
//        // 如果查询结果为空，则直接返回空列表
//        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(list)) {
//            return buildArticleListVo(records, pageParam.getPageSize());
//        }
//
//        // 将阅读量信息列表转换为文章ID列表
//        List<Long> ids = list.stream()
//                .map(ReadCount::getDocumentId)
//                .collect(Collectors.toList());
//        // 根据文章ID列表查询文章详情
//        List<Article> result = articleMapper.selectBatchIds(ids);
//        // 根据文章ID在阅读量信息列表中的顺序，对文章进行排序
//        result.sort((o1, o2) -> {
//            int i1 = ids.indexOf(o1.getId());
//            int i2 = ids.indexOf(o2.getId());
//            return Integer.compare(i1, i2);
//        });
//
//        return buildArticleListVo(records, pageParam.getPageSize());
//    }

//    @Override
//    public List<SimpleArticleDTO> querySimpleArticleBySearchKey(String key) {
//        // todo 当key为空时，返回热门推荐
//        if (StringUtils.isBlank(key)) {
//            return Collections.emptyList();
//        }
//        key = key.trim();
//        if (!openES) {
//            List<ArticleDO> records = articleDao.listSimpleArticlesByBySearchKey(key);
//            return records.stream().map(s -> new SimpleArticleDTO().setId(s.getId()).setTitle(s.getTitle()))
//                    .collect(Collectors.toList());
//        }
//        // TODO ES整合
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(key,
//                EsFieldConstant.ES_FIELD_TITLE,
//                EsFieldConstant.ES_FIELD_SHORT_TITLE);
//        searchSourceBuilder.query(multiMatchQueryBuilder);
//
//        SearchRequest searchRequest = new SearchRequest(new String[]{EsIndexConstant.ES_INDEX_ARTICLE},
//                searchSourceBuilder);
//        SearchResponse searchResponse = null;
//        try {
//            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        SearchHits hits = searchResponse.getHits();
//        SearchHit[] hitsList = hits.getHits();
//        List<Integer> ids = new ArrayList<>();
//        for (SearchHit documentFields : hitsList) {
//            ids.add(Integer.parseInt(documentFields.getId()));
//        }
//        if (ObjectUtils.isEmpty(ids)) {
//            return null;
//        }
//        List<ArticleDO> records = articleDao.selectByIds(ids);
//        return records.stream().map(s -> new SimpleArticleDTO().setId(s.getId()).setTitle(s.getTitle()))
//                .collect(Collectors.toList());
//    }

    @Override
    public CommonPageListVo<ArticleDTO> listArticlesBySearchKey(String key, CommonPageParam pageParam) {

        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Article::getStatus, PublishStatusEnum.PUBLISHED.getCode())
                .and(!StringUtils.isEmpty(key),
                        v -> v.like(Article::getTitle, key)
                                .or()
                                .like(Article::getSummary, key));
        wrapper.last(CommonPageParam.getLimitSql(pageParam))
                .orderByDesc(Article::getId);
        List<Article> records = articleMapper.selectList(wrapper);

        return buildArticleListVo(records, pageParam.getPageSize());
    }


//    @Override
//    public CommonPageListVo<ArticleDTO> listArticlesByUserAndType(Long userId, CommonPageParam pageParam, HomeSelectEnum select) {
//        List<Article> records = null;
//        if (select == HomeSelectEnum.ARTICLE) {
//            // 用户的文章列表
//            records = this.getArticlesByUserId(userId, pageParam);
//        } else if (select == HomeSelectEnum.READ) {
//            // 用户的阅读记录
//            List<Long> articleIds = userFootService.listReadedAIdsByUId(userId, pageParam);
//            records = CollectionUtils.isEmpty(articleIds) ? Collections.emptyList() : articleMapper.selectBatchIds(articleIds);
//            records = sortByIds(articleIds, records);
//        } else if (select == HomeSelectEnum.COLLECTION) {
//            // 用户的收藏列表
//            List<Long> articleIds = userFootService.listCollectionedAIdsByUId(userId, pageParam);
//            records = CollectionUtils.isEmpty(articleIds) ? Collections.emptyList() : articleMapper.selectBatchIds(articleIds);
//            records = sortByIds(articleIds, records);
//        }
//
//        if (CollectionUtils.isEmpty(records)) {
//            return CommonPageListVo.emptyVo();
//        }
//        return buildArticleListVo(records, pageParam.getPageSize());
//    }

    private List<Article> getArticlesByUserId(Long userId, CommonPageParam pageParam) {
        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Article::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Article::getAuthorId, userId)
                .last(CommonPageParam.getLimitSql(pageParam))
                .orderByDesc(Article::getId);
        if (!Objects.equals(ReqInfoContext.getReqInfo().getUserId(), userId)) {
            // 作者本人，可以查看草稿、审核、上线文章；其他用户，只能查看上线的文章
            wrapper.eq(Article::getStatus, PublishStatusEnum.PUBLISHED.getCode());
        }
        return articleMapper.selectList(wrapper);
    }


    /**
     * 根据文章ID列表对文章进行排序。
     * 此方法接收一个文章ID列表和一个文章记录列表，返回一个根据ID列表排序的文章列表。
     * 主要用于在显示文章列表时，确保文章的显示顺序与用户请求的顺序一致。
     *
     * @param articleIds 文章ID列表，用于指定排序顺序。
     * @param records 文章记录列表，包含所有待排序的文章。
     * @return 排序后的文章列表，顺序由articleIds指定。
     */
    private List<Article> sortByIds(List<Long> articleIds, List<Article> records) {
        // 初始化一个空的文章列表，用于存放排序后的文章。
        List<Article> articles = new ArrayList<>();
        // 使用流将文章记录列表转换为映射，映射的键为文章ID，值为对应的文章对象。
        // 这样做是为了快速根据文章ID查找文章对象。
        Map<Long, Article> articleMap = records.stream()
                .collect(Collectors.toMap(Article::getId, t -> t));
        // 遍历文章ID列表，如果映射中包含当前ID，则将对应的文章添加到排序后的文章列表中。
        articleIds.forEach(articleId -> {
            if (articleMap.containsKey(articleId)) {
                articles.add(articleMap.get(articleId));
            }
        });
        // 返回排序后的文章列表。
        return articles;
    }



    @Override
    public CommonPageListVo<SimpleArticleDTO> listHotArticlesForRecommend(CommonPageParam pageParam) {
        List<SimpleArticleDTO> list = articleMapper.listArticlesByReadCounts(pageParam);
        return CommonPageListVo.newVo(list, pageParam.getPageSize());
    }


    /**
     * 根据作者ID获取文章数量。
     * 此方法用于查询在线状态且未被删除的文章数量，可选地根据作者ID进行筛选。
     *
     * @param authorId 作者的ID。如果为null，则查询所有作者的文章数量。
     * @return 符合条件的文章总数。
     */
    @Override
    public Long getCountByAuthorId(Long authorId){

        // 创建Lambda查询包装器，用于构建查询条件。
        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery();
        // 设置查询条件：文章状态为在线，且未被删除。
        wrapper.eq(Article::getStatus, PublishStatusEnum.PUBLISHED.getCode())
                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode());

        // 如果提供了作者ID，则进一步筛选该作者的文章。
        if (authorId!=null){
            wrapper.eq(Article::getAuthorId, authorId);
        }

        // 根据构建的查询条件，查询文章总数。
        return articleMapper.selectCount(wrapper);

    }


//    /**
//     * 查询文章关联推荐列表
//     *
//     * @param articleId
//     * @param page
//     * @return
//     */
//    @Override
//    public CommonPageListVo<ArticleDTO> relatedRecommend(Long articleId, CommonPageParam page) {
//        Article article = this.getById(articleId);
//        if (article == null) {
//            return CommonPageListVo.emptyVo();
//        }
//
//        LambdaQueryWrapper<ArticleTag> wrapper=new LambdaQueryWrapper<>();
//        wrapper.eq(ArticleTag::getArticleId, articleId)
//                .eq(ArticleTag::getDeleted, CommonDeletedEnum.NO.getCode());
//
//        List<Long> tagIds = articleTagMapper.selectList(wrapper).stream()
//                .map(ArticleTag::getTagId)
//                .collect(Collectors.toList());
//
//        if (CollectionUtils.isEmpty(tagIds)) {
//            return CommonPageListVo.emptyVo();
//        }
//
//
//
//
//        List<Article> recommendArticles = this.listRelatedArticlesOrderByReadCount(article.getCategoryId(), tagIds, page);
//        if (recommendArticles.removeIf(s -> s.getId().equals(articleId))) {
//            // 移除推荐列表中的当前文章
//            page.setPageSize(page.getPageSize() - 1);
//        }
//        return this.buildArticleListVo(recommendArticles, page.getPageSize());
//    }


    @Override
    public List<SimpleArticleDTO> listArticlesOrderById(long lastId, int scanSize){
        return articleMapper.listArticlesOrderById(lastId, scanSize);
    }

    @Override
    public List<ArticleTag> listTagsByArticleId(Long articleId){
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, articleId)
                .eq(ArticleTag::getDeleted, CommonDeletedEnum.NO.getCode());

        return articleTagMapper.selectList(wrapper);
    }

//    @Override
//    public List<Article> listRelatedArticlesOrderByReadCount(Long categoryId, List<Long> tagIds, CommonPageParam pageParam){
//        // 根据类别ID和标签ID列表查询文章的阅读量信息
//        List<ReadCount> list = articleMapper.listArticleByCategoryAndTags(categoryId, tagIds, pageParam);
//        // 如果查询结果为空，则直接返回空列表
//        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(list)) {
//            return new ArrayList<>();
//        }
//
//        // 将阅读量信息列表转换为文章ID列表
//        List<Long> ids = list.stream()
//                .map(ReadCount::getDocumentId)
//                .collect(Collectors.toList());
//        // 根据文章ID列表查询文章详情
//        List<Article> result = articleMapper.selectBatchIds(ids);
//        // 根据文章ID在阅读量信息列表中的顺序，对文章进行排序
//        result.sort((o1, o2) -> {
//            int i1 = ids.indexOf(o1.getId());
//            int i2 = ids.indexOf(o2.getId());
//            return Integer.compare(i1, i2);
//        });
//        return result;
//    }

}
