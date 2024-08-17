package com.hpl.controller.article;

import com.hpl.article.pojo.dto.*;
import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.enums.PublishStatusEnum;
import com.hpl.article.pojo.vo.ArticleListDTO;
import com.hpl.article.pojo.dto.CategoryDTO;
import com.hpl.article.service.ArticleReadService;
import com.hpl.article.service.ArticleService;
import com.hpl.article.service.CategoryService;
import com.hpl.article.service.TagService;
import com.hpl.pojo.CommonController;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonResult;
import com.hpl.redis.RedisClient;
import com.hpl.user.context.ReqInfoContext;
import com.hpl.user.permission.Permission;
import com.hpl.user.permission.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/24 9:34
 */
/**
 * 文章列表视图
 *
 * @author yihui
 */
@RequestMapping(path = "article")
@RestController
@Tag(name = "文章相关操作")
@Slf4j
public class ArticleController extends CommonController {

    @Autowired
    private ArticleReadService articleService1;

    @Resource
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    @Resource
    private RedisClient redisClient;


    @Operation(summary = "列表查询我的文章")
    @GetMapping(path = "myself-list")
    @Permission(role = UserRole.USER)
    public CommonResult<?> myselfList(@RequestBody(required = false) SearchMyArticleDTO searchMyArticleDTO) {
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        List<MyArticleListDTO> list =articleService.listMyArticles(searchMyArticleDTO,userId);

        return CommonResult.data(list);
    }

    @Operation(summary = "新增或更新文章")
    @PostMapping
    @Permission(role = UserRole.USER)
    public CommonResult<?> addArticle(@RequestBody ArticlePostDTO articlePostDTO) {
        Long userId = ReqInfoContext.getReqInfo().getUserId();

        Long articleId =articleService.saveOrUpdate(articlePostDTO,userId);
        return CommonResult.data(articleId);
    }


    @GetMapping(path = "categories")
    @Operation(summary = "获取所有文章分类")
    public CommonResult<?> getCategories(){
        List<CategoryDTO> res= categoryService.getAllCategories();
        return CommonResult.data(res);
    }


    /**
     * 查询某个分类下的文章列表
     *
     * @param category
     * @return
     */
    @GetMapping(path = "category/{category}")
    @Operation(summary = "查询某个分类下的文章列表")
    public CommonResult<?> categoryList(@PathVariable("category") String category) {
        Long categoryId = categoryService.getIdByName(category);

        CommonPageListVo<ArticleListDTO> list = articleService.listArticlesByCategory(categoryId, CommonPageParam.newInstance());
        return CommonResult.data(list);
    }




    @GetMapping(path = "/detail/{articleId}")
    @Operation(summary = "获取文章详情")
    public CommonResult<?> getArticleDetail(@PathVariable("articleId") Long articleId) {
        ArticleDTO article = articleService.getArticleInfoById(articleId);

//        //返回给前端页面是，将文章内容由md格式转为html格式
//        article.setContent(MarkdownConverter.markdownToHtml(article.getContent()));
        return CommonResult.data(article);
    }

    @GetMapping(path = "/top-four/author")
    @Operation(summary = "获取作者排行")
    public CommonResult<?> getTopAuthor(@RequestParam(required = true) String categoryName) {

        //根据名称获取分类id
        Long categoryId = categoryService.getIdByName(categoryName);
        log.warn("categoryId:{}", categoryId);
        log.warn("作者前四呢");

        List<TopAuthorDTO> topAuthorsDTO = articleService.getTopFourAuthor(categoryId);
        return CommonResult.data(topAuthorsDTO);
    }

    @GetMapping(path = "/top-eight/")
    @Operation(summary = "获取文章排行")
    public CommonResult<?> getTopEight() {
        log.warn("文章前八呢");
        
        List<TopArticleDTO> topEight = articleService.getTopEight();
        return CommonResult.data(topEight);
    }

    @Operation(summary = "发布文章")
    @PutMapping("/publish/{articleId}")
    @Permission(role = UserRole.USER)
    public CommonResult<?> publishArticle(@PathVariable("articleId") Long articleId) {
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        Article article = articleService.getById(articleId);

        if(article.getAuthorId().equals(userId)){
            article.setStatus(PublishStatusEnum.PUBLISHED.getCode());
            articleService.updateById(article);
            redisClient.del("article:"+articleId);
            return CommonResult.success("发布成功");
        }else{
            return CommonResult.error("您不是文章作者，您没有权限发布该文章");
        }
    }

    @Operation(summary = "取消发布文章")
    @PutMapping("/un-publish/{articleId}")
    @Permission(role = UserRole.USER)
    public CommonResult<?> unPublishArticle(@PathVariable("articleId") Long articleId) {
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        Article article = articleService.getById(articleId);

        if(article.getAuthorId().equals(userId)){
            article.setStatus(PublishStatusEnum.UN_PUBLISHED.getCode());
            articleService.updateById(article);
            redisClient.del("article:"+articleId);
            return CommonResult.success("取消发布成功");
        }else{
            return CommonResult.error("您不是文章作者，您没有权限取消发布该文章");
        }
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/delete/{articleId}")
    @Permission(role = UserRole.USER)
    public CommonResult<?> deleteArticle(@PathVariable("articleId") Long articleId) {
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        articleService.deleteArticle(articleId, userId);

        return CommonResult.success("删除成功");
    }

    @Operation(summary = "获取所有标签")
    @GetMapping("/tags")
    public CommonResult<?> getTags() {
        List<TagDTO> tags = tagService.getAllTags();

        return CommonResult.data(tags);
    }


    @Operation(summary = "获取简单文章详细")
    @GetMapping("/simple-detail/{articleId}")
    public CommonResult<?> getSimpleArticleDetail(@PathVariable("articleId") Long articleId){
        SimpleDetailDTO simpleDetailDTO = articleService.getSimpleArticleDetail(articleId);
        return CommonResult.data(simpleDetailDTO);
    }



}